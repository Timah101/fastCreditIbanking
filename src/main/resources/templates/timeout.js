const idleTimeoutMilliseconds = 3000; // 5 minutes in milliseconds
let timeoutTimer;

function resetTimer() {
    clearTimeout(timeoutTimer);
    timeoutTimer = setTimeout(logout, idleTimeoutMilliseconds);
}

function logout() {
    window.location.href = '/login'
}

// Add event listeners to reset the timer on user activity
document.addEventListener("mousemove", resetTimer);
document.addEventListener("keydown", resetTimer);

