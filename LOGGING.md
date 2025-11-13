# ChoreoCam Logging Guide

Comprehensive logging has been implemented across the entire ChoreoCam application for effective error tracking and debugging.

## Android App Logging

### Framework: Timber

The Android app uses **Timber** for structured logging with the following features:

#### Log Levels
- **DEBUG** (Timber.d): Detailed information for debugging
- **INFO** (Timber.i): General informational messages
- **WARNING** (Timber.w): Warning messages
- **ERROR** (Timber.e): Error messages with exceptions

#### Configuration

Logging is configured in `ChoreoCamApplication.java`:

```java
// Debug mode: Logs to LogCat
if (BuildConfig.DEBUG) {
    Timber.plant(new Timber.DebugTree());
}
// Release mode: Logs to Firebase Crashlytics
else {
    Timber.plant(new CrashReportingTree());
}
```

#### Viewing Logs

**Development (Debug Build):**
```bash
# View all logs
adb logcat -s ChoreoCam

# View specific tag
adb logcat -s MainActivity

# Filter by log level
adb logcat *:E  # Errors only
adb logcat *:W  # Warnings and above
```

**Production (Release Build):**
- Logs are sent to Firebase Crashlytics
- View in Firebase Console → Crashlytics → Dashboard
- Only WARNING and ERROR levels are logged in production

#### Logged Components

1. **Application Lifecycle**
   - App startup/shutdown
   - Configuration loading
   - Database initialization
   - AdMob initialization
   - Sync worker scheduling

2. **Activities**
   - onCreate, onResume, onPause, onDestroy
   - User interactions (button clicks)
   - Navigation between screens
   - Permission requests/results

3. **API Client**
   - HTTP requests (method, URL)
   - HTTP responses (status code, time)
   - Network errors
   - Authentication tokens

4. **Database Operations**
   - Queries executed
   - Insert/Update/Delete operations
   - Sync status changes

5. **Background Workers**
   - Sync operations
   - Network availability checks
   - Retry attempts

6. **Error Scenarios**
   - Network failures
   - Database errors
   - Permission denied
   - Invalid configurations

### Example Log Statements

```java
// Information
Timber.i("User logged in successfully: %s", user.getEmail());

// Debug
Timber.d("Loading projects from database");

// Warning
Timber.w("Sync failed, will retry");

// Error with exception
Timber.e(exception, "Failed to load data");
```

## Backend Server Logging

### Framework: Python logging module

The backend uses Python's built-in logging with structured output.

#### Log Levels
- **DEBUG**: Detailed diagnostic information
- **INFO**: General informational messages
- **WARNING**: Warning messages
- **ERROR**: Error messages

#### Configuration

Logging is configured in `app/core/logging_config.py`:

- **File Logs**: `logs/choreocam_YYYYMMDD.log`
- **Error Logs**: `logs/choreocam_errors_YYYYMMDD.log`
- **Console**: INFO level and above

#### Log Format

```
2024-11-13 10:30:45 - choreocam.auth - INFO - register:27 - Registration attempt for email: user@example.com
```

Format: `timestamp - logger_name - level - function:line - message`

#### Viewing Logs

**Development:**
```bash
# View real-time logs
tail -f backend/logs/choreocam_$(date +%Y%m%d).log

# View only errors
tail -f backend/logs/choreocam_errors_$(date +%Y%m%d).log

# Search logs
grep "sync" backend/logs/choreocam_*.log
```

**Docker:**
```bash
# View container logs
docker-compose logs -f backend

# View last 100 lines
docker-compose logs --tail=100 backend
```

#### Logged Components

1. **API Requests**
   - Request ID
   - HTTP method and path
   - Client IP address
   - Response time
   - Status code

2. **Authentication**
   - Registration attempts
   - Login attempts (success/failure)
   - Token generation
   - Password verification

3. **Sync Operations**
   - Sync requests received
   - Users synced
   - Projects synced
   - Database commits

4. **Database Operations**
   - Query execution
   - Transactions
   - Rollbacks

5. **Errors**
   - HTTP exceptions
   - Database errors
   - Validation errors
   - Full stack traces (DEBUG level)

### Example Log Statements

```python
# Information
logger.info(f"User registered successfully: {user.email}")

# Debug
logger.debug(f"Syncing {len(projects)} projects")

# Warning
logger.warning(f"Login failed: User not found - {email}")

# Error with traceback
logger.error(f"Sync failed: {str(e)}")
logger.debug(traceback.format_exc())
```

## Log Rotation

### Android
- Managed by Timber and LogCat
- No manual rotation needed
- Firebase Crashlytics retains logs per their policy

### Backend
Logs rotate daily automatically:
- New file created each day
- Old logs preserved
- Manual cleanup script:

```bash
# Delete logs older than 30 days
find backend/logs -name "*.log" -mtime +30 -delete
```

## Troubleshooting with Logs

### Common Issues

#### 1. App Crashes
**Android:**
```bash
adb logcat *:E | grep "AndroidRuntime"
```
Check Firebase Crashlytics dashboard for crash reports.

**Backend:**
```bash
grep "ERROR" backend/logs/choreocam_errors_*.log
```

#### 2. Network Issues
**Android:**
```bash
adb logcat -s HTTP
adb logcat | grep "ApiClient"
```

**Backend:**
```bash
grep "Request failed" backend/logs/choreocam_*.log
```

#### 3. Sync Problems
**Android:**
```bash
adb logcat -s SyncWorker
```

**Backend:**
```bash
grep "sync" backend/logs/choreocam_*.log
```

#### 4. Authentication Issues
**Android:**
```bash
adb logcat -s AuthActivity
adb logcat | grep "token"
```

**Backend:**
```bash
grep "auth" backend/logs/choreocam_*.log
```

## Production Monitoring

### Android
1. Enable Firebase Crashlytics
2. Monitor crash-free rate
3. Review custom logs in Crashlytics
4. Set up alerts for critical errors

### Backend
1. Centralized logging (e.g., ELK Stack, Datadog)
2. Log aggregation from multiple instances
3. Real-time alerting
4. Performance monitoring

### Recommended Setup

```bash
# Install log monitoring (optional)
pip install python-logging-elasticsearch

# Configure in production
export LOG_LEVEL=INFO
export LOG_TO_FILE=true
export LOG_TO_ELASTICSEARCH=true
```

## Log Levels by Environment

### Development
- Android: DEBUG
- Backend: DEBUG

### Production
- Android: WARNING + ERROR (to Crashlytics)
- Backend: INFO + WARNING + ERROR

## Privacy Considerations

**What is logged:**
- User actions
- API calls
- Error messages
- Performance metrics

**What is NOT logged:**
- Passwords (plain text)
- Full credit card numbers
- Personal identification details
- User media content

## Performance Impact

- **Android**: Minimal impact (<1% CPU)
- **Backend**: <5ms per request
- **Storage**: ~50MB per day (typical usage)

## Best Practices

1. **Use appropriate log levels**
   - DEBUG: Temporary debugging info
   - INFO: Normal operations
   - WARNING: Unexpected but recoverable
   - ERROR: Failures requiring attention

2. **Include context**
   ```java
   Timber.d("User %s performed action %s", userId, action);
   ```

3. **Don't log sensitive data**
   ```java
   // BAD
   Timber.d("Password: %s", password);

   // GOOD
   Timber.d("Authentication attempt for user: %s", email);
   ```

4. **Use structured logging**
   ```python
   logger.info(f"Sync completed. Users: {users_synced}, Projects: {projects_synced}")
   ```

5. **Clean up regularly**
   - Archive old logs
   - Delete logs older than retention policy

## Support

For logging issues:
- Check log file permissions
- Verify disk space
- Review logging configuration
- Check Firebase/Crashlytics setup

---

**Last Updated**: November 2024
