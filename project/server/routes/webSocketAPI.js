var io = null;
var wsClientList = [];

module.exports = function (server) {

    var socketIo = require('socket.io');
    io = new socketIo(server);

    /* Web Socket */
    io.on('connection', function (socket) {

        wsClientList.push({ id: socket.id });

        socket.on('disconnect', function (data) {

            const removeIndex = wsClientList.findIndex(function (item) { return item.id === socket.id });
            if (removeIndex > -1) {
                wsClientList.splice(removeIndex, 1);
            }
        });
    });
}

function sendToWSClient(event, data) {

    wsClientList.forEach(function (item) {
        io.sockets.connected[item.id].emit(event, data);
    });
}


/* Web Socket Event API */
module.exports.updateCheerData = function (data) {
    sendToWSClient("UpdateCheerDataEvent", true);
}