# Hackathon Presentation & Live Demo Script — CheckoutProof AI

**Project Name:** CheckoutProof AI  
**Tagline:** *Reliable Checkout. Verified Every Time.*  
**Presenter:** Full-Stack & QA Engineering Team  
**Target Duration:** 5 to 7 Minutes  
**Audience:** Hackathon Judges, SDET Leads, Engineering Evaluators  

---

## 1. Pre-Demo Environment Checklist

Ensure the two services are active prior to starting the demonstration:
1. **Frontend Checkout App:** `http://127.0.0.1:5173` (React 19 + Vite 6)
2. **AI Failure Triage Service:** `http://127.0.0.1:8000` (FastAPI + Google Gemini API)
3. **Microsoft Edge Browser:** Bundled driver in `automation/drivers/msedgedriver.exe`

---

## 2. Timed Demo Walkthrough

### Part 1: Problem Statement & Value Proposition (0:00 - 1:00)
* **What to say:**
  > *"Every year, e-commerce platforms lose billions to checkout abandonment, flaky validation, and silent arithmetic bugs. At the same time, SDET teams spend up to 40% of their sprints deciphering massive CI/CD failure logs.*
  > 
  > *We built **CheckoutProof AI** to solve both problems: a bulletproof checkout single-page app paired with automated Selenium browser tests on Microsoft Edge, backed by an intelligent failure triage microservice powered by Google Gemini."*

---

### Part 2: Interactive Frontend Demonstration (1:00 - 2:30)
* **What to do:** Open `http://127.0.0.1:5173` in Microsoft Edge.
* **Demonstration Steps:**
  1. **Show Empty Cart Prevention:**
     * Type `"Alice Cooper"` in the customer name field.
     * Click **"Place Order Securely"** without adding any items.
     * **Point out:** The red validation banner appears: *"Your cart is empty. Please add at least one product before placing an order."* Note that the customer name is preserved so the user doesn't have to retype it!
  2. **Demonstrate Arithmetic Accuracy & Reactivity:**
     * Click `+ Add to Cart` on **DevOps Cloud Pass** ($49.00).
     * Click the `+` button to increment quantity to 2 -> Line subtotal immediately updates to **$98.00**.
     * Click `+ Add to Cart` on **SDET Automation Toolkit** ($29.00, qty 1) -> Line subtotal shows **$29.00**.
     * **Point out:** The final order total dynamically and accurately calculates to **$127.00**.
  3. **Show Multi-Click Idempotency:**
     * Rapidly click the **"Place Order Securely"** button multiple times.
     * **Point out:** The button instantly enters a disabled state with a loading spinner (`Processing Order...`). Exactly one order is confirmed with a unique reference number (`CP-XXXXXX`).

---

### Part 3: Automated Browser Testing with Selenium (2:30 - 4:00)
* **What to say:**
  > *"Manual testing proves the UI works once; automated testing guarantees it stays working on every commit. Let's run our automated Selenium suite against Microsoft Edge."*
* **Execute in Terminal:**
  ```cmd
  cd C:\Users\bakya\.gemini\antigravity\scratch\checkoutproof-ai\automation
  mvn test
  ```
* **What judges will observe:**
  * Maven builds and launches Microsoft Edge in `--inprivate` mode.
  * Three independent automated tests execute:
    1. `testBlankCustomerNameValidation` -> **PASS**
    2. `testQuantityChangeAndExactTotalVerification` ($127.00 exact total) -> **PASS**
    3. `testSuccessfulCheckout` (verifies `CP-` reference number) -> **PASS**
  * **Final Result:** `Tests run: 3, Failures: 0, Errors: 0, Skipped: 0`, Maven exit code `0`.
* **Show Evidence:** Open `reports/automation/index.html` in the browser to showcase the TestNG execution report.

---

### Part 4: AI Failure Triage & Quality Dashboard (4:00 - 5:30)
* **What to say:**
  > *"When an automated test fails in CI/CD, developers usually have to sift through 500 lines of Java stack traces. CheckoutProof AI intercepts the failure, scrubs all sensitive data, and delivers an instant triage report powered by Google Gemini 2.5 Flash."*
* **What to show:** Open `http://127.0.0.1:8000/` in the browser.
* **Key Features to Highlight:**
  1. **Authoritative Truth Boundary Banner:**
     * Point out the banner at the top: Selenium WebDriver assertions remain the **sole authority** for test verdicts. AI triage is advisory, maintaining strict SDET discipline.
  2. **Section 1 — Automated Selenium Execution Summary:**
     * Shows live stats parsed directly from `testng-results.xml`: 3 Total, 3 Passed, 0 Failed, 100% Suite Pass, Duration: ~11s.
  3. **Section 2 — AI Failure Triage & Diagnostic Hub:**
     * Demonstrate the filter buttons:
       * **Filter by Source:** Toggle between *All Sources*, *Selenium Automated*, and *Manual / Demo API*.
       * **Filter by Engine:** Toggle between *Gemini 2.5* and *Mock Demo*.
     * Point out the diagnostic cards:
       * **Execution ID & Timestamp**: Complete audit traceability.
       * **Diagnosed Root Cause**: Clear explanation of what went wrong.
       * **Actionable Debugging Steps**: Step-by-step guidance for developers.
       * **Jira-Ready Bug Report**: Formatted title, severity, reproduction steps, expected vs actual behavior.
  4. **Clear Demo Reports Action:**
     * Click **"✕ Clear Demo Reports"** to show that sample/demo reports are safely cleared while genuine Selenium automation reports are strictly preserved.
  5. **Interactive Swagger Docs:**
     * Click the top-right link to open `http://127.0.0.1:8000/docs` and show the OpenAPI REST endpoints.

---

### Part 5: Conclusion & Q&A (5:30 - 6:00)
* **Summary Statement:**
  > *"CheckoutProof AI delivers a complete, cohesive, end-to-end SDET platform: a robust React checkout application, 100% passing automated regression tests on Microsoft Edge, and next-generation Google Gemini AI failure analysis."*
