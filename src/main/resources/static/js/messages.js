document.addEventListener('DOMContentLoaded', function() {
    const messagesContainer = document.getElementById('messages-container');
    const connectionStatus = document.getElementById('connection-status');
    const clearMessagesBtn = document.getElementById('clear-messages');
    const datetimeCount = document.getElementById('datetime-count');
    const valueCount = document.getElementById('value-count');
    const totalCount = document.getElementById('total-count');
    const showLocalTimeSwitch = document.getElementById('show-local-time');
    const timezoneInfo = document.getElementById('timezone-info');

    let stompClient = null;
    let useLocalTime = false;

    function formatDateTime(utcDateStr, useLocal) {
        if (!utcDateStr) return '';

        const date = new Date(utcDateStr);

        if (useLocal) {
            // локальное время
            timezoneInfo.textContent = Intl.DateTimeFormat().resolvedOptions().timeZone;
            return date.toLocaleString();
        } else {
            // время в utc
            timezoneInfo.textContent = 'UTC';
            return utcDateStr;
        }
    }

    function updateTimeDisplay() {
        document.querySelectorAll('.datetime-value').forEach(element => {
            const utcDate = element.getAttribute('data-utc');
            element.textContent = formatDateTime(utcDate, useLocalTime);
        });
    }

    showLocalTimeSwitch.addEventListener('change', function() {
        useLocalTime = this.checked;
        updateTimeDisplay();
    });

    function connect() {
        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, function(frame) {
            console.log('Connected: ' + frame);
            connectionStatus.className = 'alert alert-success';
            connectionStatus.textContent = 'Connected to WebSocket';

            stompClient.subscribe('/topic/messages', function(message) {
                const messageData = JSON.parse(message.body);
                addMessage(messageData);
                updateCounters(messageData);
            });

            stompClient.subscribe('/topic/status', function(message) {
                console.log('Status message received:', message.body);
            });

            stompClient.send("/app/messages.connect", {}, "Connect");
        }, function(error) {
            console.error('Error connecting to WebSocket:', error);
            connectionStatus.className = 'alert alert-danger';
            connectionStatus.textContent = 'Connection Error: ' + error;

            // retry
            setTimeout(connect, 5000);
        });
    }

    function addMessage(message) {
        const messageElement = document.createElement('div');
        messageElement.className = 'message-row new-message ' +
            (message.type === 'DATETIME' ? 'datetime-message' : 'value-message');

        let content = '';
        if (message.type === 'DATETIME') {
            content = `
                <strong>DateTime Message</strong>
                <div>Timestamp: <span class="datetime-value" data-utc="${message.dateTime}">${formatDateTime(message.dateTime, useLocalTime)}</span></div>
                <div>Received: <span class="datetime-value" data-utc="${message.receivedAt}">${formatDateTime(message.receivedAt, useLocalTime)}</span></div>
            `;
        } else if (message.type === 'VALUE') {
            content = `
                <strong>Value Message</strong>
                <div>ID: ${message.id}</div>
                <div>Value: ${message.value}</div>
                <div>Received: <span class="datetime-value" data-utc="${message.receivedAt}">${formatDateTime(message.receivedAt, useLocalTime)}</span></div>
            `;
        } else {
            content = `
                <strong>Unknown Message Type: ${message.type}</strong>
                <div>Content: ${JSON.stringify(message)}</div>
            `;
        }

        messageElement.innerHTML = content;
        messagesContainer.insertBefore(messageElement, messagesContainer.firstChild);

        if (messagesContainer.children.length > 50) {
            messagesContainer.removeChild(messagesContainer.lastChild);
        }
    }

    function updateCounters(message) {
        if (message.type === 'DATETIME') {
            datetimeCount.textContent = parseInt(datetimeCount.textContent) + 1;
        } else if (message.type === 'VALUE') {
            valueCount.textContent = parseInt(valueCount.textContent) + 1;
        }

        totalCount.textContent = parseInt(datetimeCount.textContent) + parseInt(valueCount.textContent);
    }

    clearMessagesBtn.addEventListener('click', function() {
        messagesContainer.innerHTML = '';
    });

    connect();
    updateTimeDisplay();
});