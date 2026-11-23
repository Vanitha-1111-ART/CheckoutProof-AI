import re
from typing import Tuple

# Regex patterns for sensitive data scrubbing
EMAIL_REGEX = re.compile(r'\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,7}\b')
CREDIT_CARD_REGEX = re.compile(r'\b(?:\d{4}[ -]?){3}\d{4}\b')
BEARER_TOKEN_REGEX = re.compile(r'(?i)(bearer\s+[a-zA-Z0-9_\-\.]+)|(api[-_]?key\s*[:=]\s*["\']?[a-zA-Z0-9_\-]+)')
PASSWORD_REGEX = re.compile(r'(?i)(password|secret|passwd)\s*[:=]\s*["\']?[^"\',\s]+')
PATH_USER_REGEX = re.compile(r'C:[/\\]Users[/\\][A-Za-z0-9_.\-]+')


def sanitize_text(text: str) -> str:
    """
    Sanitizes raw text to strip personal identifiable information (PII),
    passwords, secret tokens, and local file username structures.
    """
    if not text:
        return ""

    sanitized = text
    # Mask potential credit cards
    sanitized = CREDIT_CARD_REGEX.sub("[REDACTED_CREDIT_CARD]", sanitized)
    # Mask email addresses
    sanitized = EMAIL_REGEX.sub("[REDACTED_EMAIL]", sanitized)
    # Mask bearer tokens and API keys
    sanitized = BEARER_TOKEN_REGEX.sub("[REDACTED_AUTH_TOKEN]", sanitized)
    # Mask passwords
    sanitized = PASSWORD_REGEX.sub(r"\1: [REDACTED_PASSWORD]", sanitized)
    # Mask local user directory names
    sanitized = PATH_USER_REGEX.sub(r"C:\\Users\\<USER_DIR>", sanitized)

    return sanitized


def sanitize_payload(
    error_message: str,
    stack_trace: str,
    expected: str = "",
    actual: str = ""
) -> Tuple[str, str, str, str]:
    """Sanitizes all failure text fields prior to AI model processing."""
    return (
        sanitize_text(error_message),
        sanitize_text(stack_trace),
        sanitize_text(expected),
        sanitize_text(actual),
    )
