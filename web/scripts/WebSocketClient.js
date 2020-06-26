ws.onerror = function (event) {
    console.log(event);
}
var userList;
ws.onmessage = function (event) {
    console.log(event.data);
    var data = JSON.parse(event.data);
    document.getElementById("log").value += "[" + timestamp() + "] " + data.message + "\n";
    userList = data.users;
    displayOnlineUsers(data.users, data.currentUser);
}

document.getElementById("input").addEventListener("keyup", function (event) {
    if (event.keyCode === 13) {
        var values = checkForPersonalMessage(event.target.value)
        var response = {};
        if (values[0]) {
            response = {
                "users": userList,
                "message": event.target.value.substr(event.target.value.indexOf(" ") + 1),
                "sendMessageUser": values[1]
            };
        } else {
            response = {
                "users": userList,
                "message": event.target.value
            };
        }
        console.log(JSON.stringify(response));
        ws.send(JSON.stringify(response));
        event.target.value = "";
    }
});

function timestamp() {
    var d = new Date(), minutes = d.getMinutes();
    if (minutes < 10) minutes = '0' + minutes;
    return d.getHours() + ':' + minutes;
}

function checkForPersonalMessage(message) {
    if (message.startsWith(":")) {
        var user = message.substr(1, message.indexOf(" ") - 1);
        if (userList.includes(user)) {
            return [true, user];
        }
        return [false, ""]
    } else {
        return [false, ""];
    }
}