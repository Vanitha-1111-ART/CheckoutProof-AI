import json
import os
import re
import uuid
from datetime import datetime
from pathlib import Path
from typing import Optional
from dotenv import load_dotenv

from .models import AiAnalysisResult, SuggestedBugReport, TestFailureInput
from .sanitizer import sanitize_payload

# Explicitly resolve the .env path inside ai-service/
ENV_FILE = Path(__file__).resolve().parent.parent / ".env"
load_dotenv(dotenv_path=ENV_FILE, override=True)


def is_gemini_available() -> bool:
    """Checks whether a valid, non-placeholder GEMINI_API_KEY is configured in the environment."""
    load_dotenv(dotenv_path=ENV_FILE, override=True)
    key = os.getenv("GEMINI_API_KEY", "").strip().strip("\"'")
    return bool(key and key != "your_gemini_api_key_here" and len(key) > 10)


def extract_json_from_text(raw_text: str) -> dict:
    """Extracts and parses JSON from raw text or markdown fenced blocks."""
    cleaned = raw_text.strip()
    match = re.search(r"```(?:json)?\s*([\s\S]*?)\s*```", cleaned)
    if match:
        cleaned = match.group(1).strip()
    return json.loads(cleaned)


def generate_execution_id() -> str:
    """Generates a distinctive execution identifier with timestamp and random suffix."""
    return f"EXEC-{datetime.now().strftime('%Y%m%d-%H%M%S')}-{uuid.uuid4().hex[:4].upper()}"


def generate_mock_analysis(payload: TestFailureInput, sanitized_error: str, sanitized_trace: str) -> AiAnalysisResult:
    """
    Generates high-fidelity, deterministic simulated triage analysis when no Gemini API
    key is configured or when offline. Clearly labeled as simulated.
    """
    test_name = payload.test_name.lower()
    source = payload.source or ("selenium_listener" if "selenium" in str(payload.stack_trace).lower() else "manual_api")
    test_status = payload.test_status or "ASSERTION_FAILURE"

    if "blankcustomername" in test_name or "name" in test_name:
        summary = (
            "[SIMULATED MOCK ANALYSIS] The test expected the application to validate that the Customer Name "
            "field is required, but an unexpected timeout or state occurred."
        )
        root_cause = (
            "Potential selector mismatch on the validation banner, or the assertion attempted to evaluate "
            "the visibility of the confirmation screen before the DOM updated, resulting in an unhandled TimeoutException."
        )
        suggestions = [
            "Verify that the error banner element '[data-testid=\"error-message-banner\"]' is correctly rendered in React.",
            "Ensure negative test assertions use 'invisibilityOfElementLocated' or check element absence rather than waiting for visibility.",
            "Check that customerName trimming logic in App.jsx triggers the error state when passed an empty string."
        ]
        next_steps = [
            "Update CheckoutPage.java to use non-blocking presence checks for elements expected to be absent.",
            "Add automated component tests verifying the error banner unmounts upon typing.",
            "Verify browser console logs for unhandled React state errors."
        ]
        bug_title = "Validation Banner / Screen Check Timeout on Blank Customer Name Submission"
        severity = "Medium"
    elif "quantity" in test_name or "total" in test_name:
        summary = (
            "[SIMULATED MOCK ANALYSIS] Discrepancy observed during product quantity modification or subtotal/total calculation."
        )
        root_cause = (
            "Pricing arithmetic calculation mismatch or asynchronous state latency in React setState updating cart subtotal."
        )
        suggestions = [
            "Verify individual line item subtotals: Product 1 ($49.00 * 2 = $98.00) and Product 2 ($29.00 * 1 = $29.00).",
            "Ensure the final order total matches '$127.00' exactly.",
            "Inspect currency formatting helper in CartSummary.jsx."
        ]
        next_steps = [
            "Assert individual line item subtotals before asserting the final order total.",
            "Confirm quantity buttons have debouncing or atomic state updates."
        ]
        bug_title = "Cart Subtotal / Total Calculation Discrepancy under Rapid Quantity Change"
        severity = "High"
    else:
        summary = (
            f"[SIMULATED MOCK ANALYSIS] Failure in test scenario '{payload.test_name}'. "
            f"Observed error: {sanitized_error[:120]}..."
        )
        root_cause = (
            "Likely element locator degradation, unexpected DOM restructuring, or timing race condition during transaction."
        )
        suggestions = [
            "Inspect the failure screenshot to verify what state the browser was rendered in.",
            "Check network response tabs in Edge DevTools for failed asset or API calls.",
            "Confirm that element locators in CheckoutPage.java match current frontend data-testid attributes."
        ]
        next_steps = [
            "Re-run test with headed Edge browser to visually follow the transaction flow.",
            "Add explicit WebDriverWait on intermediate state transitions."
        ]
        bug_title = f"Automation Test Failure in {payload.test_name}"
        severity = "Medium"

    return AiAnalysisResult(
        execution_id=generate_execution_id(),
        timestamp=datetime.now().isoformat(),
        test_name=payload.test_name,
        source=source,
        test_status=test_status,
        status="ANALYZED",
        analysis_mode="mock_demo",
        failure_summary=summary,
        possible_root_cause=root_cause,
        debugging_suggestions=suggestions,
        recommended_next_steps=next_steps,
        suggested_bug_report=SuggestedBugReport(
            title=bug_title,
            severity=severity,
            environment="Microsoft Edge 158 / Windows 11 / Selenium 4.28.1",
            steps_to_reproduce=[
                "1. Launch application in Edge browser.",
                f"2. Execute test actions for '{payload.test_name}'.",
                f"3. Observe failure at assertion point: {sanitized_error[:100]}."
            ],
            expected_behavior=payload.expected_result or "Action should complete without assertion failure.",
            actual_behavior=payload.actual_result or sanitized_error[:150]
        ),
        confidence_level="Simulated Demo Confidence: 85% (Based on SDET Heuristic Engine)",
        sanitized=True
    )


def analyze_with_gemini(payload: TestFailureInput) -> AiAnalysisResult:
    """
    Sends the sanitized failure details to the Google Gemini API using the official SDK.
    Falls back gracefully to mock analysis if the API key is not present or an error occurs.
    """
    sanitized_error, sanitized_trace, sanitized_expected, sanitized_actual = sanitize_payload(
        payload.error_message,
        payload.stack_trace or "",
        payload.expected_result or "",
        payload.actual_result or ""
    )

    load_dotenv(dotenv_path=ENV_FILE, override=True)

    if not is_gemini_available():
        return generate_mock_analysis(payload, sanitized_error, sanitized_trace)

    api_key = os.getenv("GEMINI_API_KEY", "").strip().strip("\"'")
    source = payload.source or ("selenium_listener" if "selenium" in str(payload.stack_trace).lower() else "manual_api")
    test_status = payload.test_status or "ASSERTION_FAILURE"

    try:
        from google import genai
        from google.genai import types

        # Explicitly configure v1beta API version to enable response_mime_type="application/json"
        client = genai.Client(
            api_key=api_key,
            http_options=types.HttpOptions(api_version="v1beta")
        )

        system_instruction = (
            "You are CheckoutProof AI, an elite SDET and Senior Full-Stack debugging specialist. "
            "Analyze the provided automated test failure from a React + Vite + Selenium + Edge environment. "
            "Produce strict JSON matching the schema: "
            "{"
            "  \"failure_summary\": \"string\","
            "  \"possible_root_cause\": \"string\","
            "  \"debugging_suggestions\": [\"string\"],"
            "  \"recommended_next_steps\": [\"string\"],"
            "  \"suggested_bug_report\": {"
            "     \"title\": \"string\","
            "     \"severity\": \"Critical|High|Medium|Low\","
            "     \"environment\": \"string\","
            "     \"steps_to_reproduce\": [\"string\"],"
            "     \"expected_behavior\": \"string\","
            "     \"actual_behavior\": \"string\""
            "  },"
            "  \"confidence_level\": \"string\""
            "}"
        )

        user_prompt = (
            f"Test Case: {payload.test_name}\n"
            f"Error Message: {sanitized_error}\n"
            f"Expected: {sanitized_expected or 'N/A'}\n"
            f"Actual: {sanitized_actual or 'N/A'}\n"
            f"Stack Trace:\n{sanitized_trace[:1500]}\n"
            f"Screenshot Path: {payload.screenshot_path or 'None'}\n"
        )

        target_model = os.getenv("GEMINI_MODEL", "gemini-2.5-flash").strip()
        candidate_models = [target_model, "gemini-2.0-flash", "gemini-1.5-flash"]
        candidate_models = list(dict.fromkeys(candidate_models))

        response = None
        last_error = None
        for model_name in candidate_models:
            try:
                response = client.models.generate_content(
                    model=model_name,
                    contents=user_prompt,
                    config=types.GenerateContentConfig(
                        system_instruction=system_instruction,
                        response_mime_type="application/json",
                        temperature=0.2
                    )
                )
                if response and response.text:
                    break
            except Exception as model_err:
                last_error = model_err
                continue

        if not response or not response.text:
            raise last_error or RuntimeError("Gemini model produced empty response")

        data = extract_json_from_text(response.text)

        return AiAnalysisResult(
            execution_id=generate_execution_id(),
            timestamp=datetime.now().isoformat(),
            test_name=payload.test_name,
            source=source,
            test_status=test_status,
            status="ANALYZED",
            analysis_mode="real_gemini",
            failure_summary=data.get("failure_summary", "Failure analyzed by Gemini."),
            possible_root_cause=data.get("possible_root_cause", "Root cause identified by model."),
            debugging_suggestions=data.get("debugging_suggestions", []),
            recommended_next_steps=data.get("recommended_next_steps", []),
            suggested_bug_report=SuggestedBugReport(**data.get("suggested_bug_report", {
                "title": f"Defect in {payload.test_name}",
                "severity": "Medium",
                "environment": "Edge 158 / Windows 11 / Selenium 4.28.1",
                "steps_to_reproduce": [f"Run {payload.test_name}"],
                "expected_behavior": sanitized_expected or "Expected success",
                "actual_behavior": sanitized_actual or sanitized_error
            })),
            confidence_level=data.get("confidence_level", "Gemini Confidence: High (92%)"),
            sanitized=True
        )

    except Exception as e:
        # Graceful fallback: return mock with warning annotation
        mock_res = generate_mock_analysis(payload, sanitized_error, sanitized_trace)
        mock_res.failure_summary = f"[GEMINI API FALLBACK: {str(e)[:80]}] " + mock_res.failure_summary
        mock_res.analysis_mode = "mock_demo (API Call Fallback)"
        return mock_res
