function displayOnlineUsers(userList, currentUser) {
    document.getElementById("list").innerHTML = "";
    for (var user of userList) {
        if (user != currentUser) {
            var el = document.getElementById("list");
            var node = document.createElement("li");
            node.innerText = user;
            el.appendChild(node);
        }
    }
}