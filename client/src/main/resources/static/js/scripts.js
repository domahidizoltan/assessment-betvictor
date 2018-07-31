var stompClient;

function initWSConnection() {
    var socket = new SockJS(wsEndpoint);
    stompClient = Stomp.over(socket);
    stompClient.connect({}, connectWSCallback, errorWSCallback);
}

function addNotification(message) {
    var operation;
    switch(message.eventType) {
        case 'CREATE': operation = 'inserted'; break;
        case 'READ': operation = 'read'; break;
        case 'UPDATE': operation = 'updated'; break;
        case 'DELETE': operation = 'deleted'; break;
        default: operation = '(unknown operation)'
    }

    var timestamp = message.instant.replace('T',' ').replace('Z','');

    var block = "<div>timestamp=" + timestamp + " :: a row with ID=" + message.key + " was " + operation + "</div>";
    $('#notifications').prepend(block);
}

function connectWSCallback(frame) {
    console.log('Connected: ' + frame);
    connectionStatus('websocket connection established!')

    stompClient.subscribe(wsTopic, function(data) {
        console.log(data);
        var message = JSON.parse(data.body);
        var payload = JSON.parse(message.payload);
        addNotification(payload);
    });

}

function errorWSCallback(error) {
     connectionStatus('<span style="red">Could not connect to websocket. Please refresh the page.</span>');
}

function connectionStatus(status) {
    $('#connection-status').html(status);
}

function getServerInfo() {
    $.get("/notification-server-info", function(data) {
        $('#server-info').html(data);
    });
    setTimeout(getServerInfo, statusPollInterval);
}

$(document).ready(function (){
    initWSConnection();
    getServerInfo();
});
