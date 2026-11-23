from typing import List, Optional
from pydantic import BaseModel, Field


class TestFailureInput(BaseModel):
    test_name: str = Field(..., description="Name of the automated test method")
    error_message: str = Field(..., description="Failure error or exception message")
    expected_result: Optional[str] = Field(None, description="Expected outcome of the assertion")
    actual_result: Optional[str] = Field(None, description="Actual outcome observed in the browser")
    stack_trace: Optional[str] = Field(None, description="Full Java or Selenium exception stack trace")
    screenshot_path: Optional[str] = Field(None, description="File path to the captured failure screenshot")
    source: Optional[str] = Field("manual_api", description="Source: 'selenium_listener' or 'manual_api'")
    test_status: Optional[str] = Field("ASSERTION_FAILURE", description="Source test verdict from assertions")


class SuggestedBugReport(BaseModel):
    title: str = Field(..., description="Concise bug title for Jira / GitHub Issues")
    severity: str = Field("Medium", description="Severity classification (Critical, High, Medium, Low)")
    environment: str = Field("Edge 158 / Windows 11 / Selenium 4.28.1", description="Test environment")
    steps_to_reproduce: List[str] = Field(..., description="Step-by-step reproduction instructions")
    expected_behavior: str = Field(..., description="Expected system behavior")
    actual_behavior: str = Field(..., description="Actual observed behavior")


class AiAnalysisResult(BaseModel):
    execution_id: str = Field(..., description="Unique execution reference ID (e.g. EXEC-20261123-050622-A1B2)")
    timestamp: str = Field(..., description="ISO 8601 timestamp of analysis")
    test_name: str = Field(..., description="Name of the test")
    source: str = Field(..., description="'selenium_listener' or 'manual_api'")
    test_status: str = Field("ASSERTION_FAILURE", description="Authoritative test status determined by Selenium assertions")
    status: str = Field("ANALYZED", description="Status of the AI triage analysis")
    analysis_mode: str = Field(..., description="'real_gemini' or 'mock_demo'")
    failure_summary: str = Field(..., description="High-level, human-readable summary of what went wrong")
    possible_root_cause: str = Field(..., description="Technical diagnosis of the underlying defect or failure")
    debugging_suggestions: List[str] = Field(..., description="Actionable debugging steps for QA/Dev")
    recommended_next_steps: List[str] = Field(..., description="Recommendations to prevent regression")
    suggested_bug_report: SuggestedBugReport
    confidence_level: str = Field(..., description="Confidence rating or uncertainty statement")
    sanitized: bool = Field(True, description="Whether input data underwent PII and credential sanitization")


class HealthResponse(BaseModel):
    status: str
    gemini_api_configured: bool
    service_version: str


class ClearDemoResponse(BaseModel):
    status: str
    cleared_count: int
    preserved_selenium_count: int
    message: str
