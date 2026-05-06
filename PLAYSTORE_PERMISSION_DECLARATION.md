# Google Play Store Permission Declaration

## App Information
- **App Name:** FocusCat
- **Package Name:** com.apnex.focuscat
- **Category:** Parenting / Productivity

## Sensitive Permissions Used

### 1. CAMERA Permission
**Permission:** `android.permission.CAMERA`

**Why we need it:**
FocusCat includes an optional "Reaction Recording" feature that allows parents to capture short 10-second videos of their child's reaction when they attempt to open a blocked app during focus time. This feature is:
- **Completely optional** - disabled by default
- **Parent-controlled** - must be explicitly enabled in Settings
- **Transparent** - small visible preview shown during recording
- **Local only** - videos never leave the device

**How it's used:**
- Front camera records 10-second clips
- Small preview (100x100px) always visible during recording
- Videos saved to app private storage
- Parents can view/share/delete recordings

**Why default camera apps won't work:**
The recording must happen automatically when the overlay appears, without requiring the child to interact with a camera app. This is why direct camera access is necessary.

---

### 2. RECORD_AUDIO Permission
**Permission:** `android.permission.RECORD_AUDIO`

**Why we need it:**
Audio permission is required to capture sound along with the video reactions. This allows parents to hear their child's reactions (laughs, comments) which makes the videos more meaningful and entertaining.

---

### 3. SYSTEM_ALERT_WINDOW (Overlay) Permission
**Permission:** `android.permission.SYSTEM_ALERT_WINDOW`

**Why we need it:**
FocusCat is a screen time management app that blocks distracting apps during focus sessions. The overlay permission is required to:
- Display the blocking screen when child opens a blocked app
- Show the friendly cat character and encouraging messages
- Provide the "Continue Focus" and "Go Home" buttons
- Maintain the focus session until time is complete

**Why this permission is essential:**
Without overlay permission, the app cannot intercept and block access to distracting apps. This is the core functionality of the app.

---

### 4. BIND_ACCESSIBILITY_SERVICE Permission
**Permission:** `android.permission.BIND_ACCESSIBILITY_SERVICE`

**Why we need it:**
The accessibility service is required to:
- Detect when the child opens a blocked app
- Trigger the overlay to block the app
- Monitor app usage for statistics

**Privacy Guarantee:**
The accessibility service is used **solely** to detect when a blocked app enters the foreground. No user input, text, or personal data is read, stored, or transmitted.

**Why this permission is essential:**
This is the only way to detect app launches and enforce the focus block on Android.

---

## Data Safety Declaration

| Data Type | Collected | Shared | Encrypted | Purpose |
|-----------|-----------|--------|-----------|---------|
| Photos/Videos | Yes (optional) | No | No (local storage) | Reaction recording feature |
| App usage data | Yes | No | No (local storage) | Focus statistics |
| User IDs | No | No | - | - |
| Location | No | No | - | - |
| Personal info | No | No | - | - |

**All data is stored locally on the device. No data is transmitted to servers.**

---

## Target Audience

**Primary:** Parents of children ages 6-16 who want to help their kids develop healthy screen time habits.

**Secondary:** Adults who want to block their own distracting apps during work/study time.

---

## Testing Instructions for Reviewers

1. Install the app
2. Grant overlay and accessibility permissions when prompted
3. Set a focus timer (e.g., 5 minutes)
4. Open Instagram or TikTok - the cat overlay should appear
5. Go to Settings → Enable Reaction Recording → Grant camera permission
6. Open a blocked app again - small camera preview should appear in corner
7. Wait 10 seconds - recording should save automatically
8. Tap "Reactions" button to view the recorded video

---

## Contact for Play Store Review Team

If you have questions about these permissions or need clarification, please contact:
[Your developer email]

We are committed to transparency and children's privacy protection.
