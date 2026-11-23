# Test Plan & Strategy — CheckoutProof AI

**Tagline:** *Reliable Checkout. Verified Every Time.*  
**Version:** 1.1.0  
**Target Application:** CheckoutProof AI E-Commerce Single Page Application  

---

## 1. Objectives & Scope

The primary objective of this test plan is to establish and verify an uncompromised quality standard for CheckoutProof AI. The scope spans:
1. **Automated Regression Suite:** Executed on Microsoft Edge 158 using Java 17 + Selenium 4 + TestNG.
2. **Business Constraints Verification:**
   - Mandatory customer full name requirement.
   - Empty cart checkout locking.
   - Arithmetic calculation integrity: individual line subtotals and final total ($127.00 exact verification).
   - Form state preservation upon validation failure.
3. **Idempotency & Concurrency:** Rapid multi-click protection preventing duplicate orders.
4. **Diagnostic Integrity:** Automated screenshot capture on failure and advisory AI failure triage via FastAPI and Google Gemini 2.5 Flash.

---

## 2. Test Environment Specification

| Component | Technical Specification |
| :--- | :--- |
| **Operating System** | Windows 11 Enterprise (64-bit) |
| **Browser** | Microsoft Edge 158.0.4370.33 (64-bit) |
| **WebDriver** | Microsoft Edge WebDriver 158.0.4370.33 |
| **Java JDK** | Eclipse Adoptium OpenJDK 17.0.21.1 |
| **Build & Test Runner** | Apache Maven 3.10.1 + TestNG 7.10.2 + Surefire 3.2.5 |
| **Selenium Framework** | Selenium WebDriver 4.28.1 (Java) |
| **Frontend Server** | Node.js v26.13.1 / Vite v6.4.6 (`http://127.0.0.1:5173`) |
| **AI Triage Microservice**| Python 3.15 / FastAPI / Uvicorn (`http://127.0.0.1:8000`) |
| **AI LLM Engine** | Google Gemini 2.5 Flash (`google-genai` SDK v1beta) |

---

## 3. Automation Strategy & SDET Standards

- **Page Object Model (POM):** All web element definitions, selectors, and user actions are encapsulated in [CheckoutPage.java](file:///C:/Users/bakya/.gemini/antigravity/scratch/checkoutproof-ai/automation/src/test/java/com/checkoutproof/pages/CheckoutPage.java). Test methods contain assertions only.
- **Selector Stability:** Zero fragile XPath or CSS class selectors; 100% of selectors rely on dedicated `data-testid` attributes.
- **Synchronization Policy:** Strict reliance on `WebDriverWait` with explicit `ExpectedConditions`. No arbitrary `Thread.sleep` statements.
- **Negative Assertion Pattern:** Negative checks (e.g. confirming that the order confirmation screen is *not* present) use non-blocking presence verification (`driver.findElements(...).isEmpty()`) to prevent artificial 10-second wait timeouts.
- **Session Isolation:** Each `@Test` method launches a brand new Edge session in `--inprivate` mode with fresh cache and cookies, and cleanly terminates in `@AfterMethod`.

---

## 4. Test Scenarios & Execution Matrix

### Automated Regression Suite (`CheckoutTests.java`)

| Test ID | Test Scenario | Execution Steps | Expected Assertion Result | Status |
| :--- | :--- | :--- | :--- | :---: |
| **TC-AUTO-01** | Successful Checkout Flow | 1. Verify initial cart is empty.<br>2. Add Product 1 ($49.00).<br>3. Enter customer name "Alex Morgan".<br>4. Click "Place Order Securely". | 1. Confirmation screen appears.<br>2. Customer Name matches "Alex Morgan".<br>3. Order reference number starts with `CP-` and length >= 8.<br>4. Confirmed order total matches "$49.00". | **PASS** |
| **TC-AUTO-02** | Blank Customer Name Validation | 1. Add Product 1 ($49.00) so cart is not empty.<br>2. Leave customer name blank.<br>3. Click "Place Order Securely". | 1. Error banner rendered containing "Customer Name is required".<br>2. Confirmation screen is NOT displayed.<br>3. User remains on checkout page with cart intact. | **PASS** |
| **TC-AUTO-03** | Quantity Increment & Total ($127.00) | 1. Add Product 1 ($49.00) and increment quantity to 2.<br>2. Add Product 2 ($29.00, quantity 1).<br>3. Inspect subtotals and final total. | 1. Product 1 subtotal equals "$98.00" (2 x $49.00).<br>2. Product 2 subtotal equals "$29.00" (1 x $29.00).<br>3. Final order total equals exactly "$127.00". | **PASS** |

### Manual & Edge-Case Verification Scenarios (`ManualVerificationRunner.java`)

| Test ID | Scenario | Steps | Expected Result | Status | Evidence |
| :--- | :--- | :--- | :--- | :---: | :--- |
| **TC-MAN-01** | Empty Cart Checkout Block | 1. Launch with empty cart.<br>2. Input name "Sam Fisher".<br>3. Click "Place Order Securely". | Order blocked; error banner states "Your cart is empty. Please add at least one product before placing an order." Customer name preserved. | **PASS** | `reports/manual-testing/manual_empty_cart_blocked.png` |
| **TC-MAN-02** | Rapid Multi-Click Idempotency | 1. Add product to cart.<br>2. Enter customer name "Jordan Reed".<br>3. Rapidly double-click "Place Order". | Button immediately disables showing animated spinner; subsequent clicks ignored; exactly 1 confirmation and 1 reference number created. | **PASS** | `reports/manual-testing/manual_rapid_click_single_order.png` |

---

## 5. Failure Triage & Severity Classification

When an automated assertion fails, `TestListener.java` captures a full-page screenshot to `reports/screenshots/` and dispatches the sanitized trace to the AI microservice.

| Severity | Definition | Example Scenario |
| :--- | :--- | :--- |
| **Critical (P1)** | Order placement blocked for valid data, or incorrect arithmetic charge. | Final total displays $98.00 instead of $127.00. |
| **High (P2)** | Idempotency failure (duplicate charge/order) or session bleeding. | Two orders placed from one click action. |
| **Medium (P3)** | Validation message formatting error, or locator timing latency. | Error banner missing required text or delayed > 3s. |
| **Low (P4)** | Cosmetic spacing or typography imperfection. | Alignment discrepancy in product badge. |
