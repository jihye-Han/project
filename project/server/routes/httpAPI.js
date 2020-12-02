const sessionUtil = require('../utility/sessionUtil')
const userDBUtil = require('../utility/userDBUtil');
const cheerDBUtil = require('../utility/cheerDBUtil');
const webSocketAPI = require('./webSocketAPI');

module.exports = function (app) {

    expressApp = app;

    /* HTTP Web API Handler */
    let httpGETList = [
        ['/getCheerInfo', getCheerInfo, sessionUtil.sConstant.LEVEL_NONE],
        ['/getCheerInfoList', getCheerInfoList, sessionUtil.sConstant.LEVEL_NONE],
    ];

    let httpPOSTList = [
        ['/adminLogIn', adminLogIn, sessionUtil.sConstant.LEVEL_NONE],
        ['/adminLogOut', adminLogOut, sessionUtil.sConstant.LEVEL_ADMIN],

        ['/saveQRCodeInfo', saveQRCodeInfo, sessionUtil.sConstant.LEVEL_ADMIN],
    ];

    /* set Web API Handler */
    httpGETList.forEach(list => {
        if (list[2] === sessionUtil.sConstant.LEVEL_NONE) {
            app.get('/api' + list[0], list[1]);
        } else {
            app.get('/api' + list[0],
                sessionUtil.apiReadSession(list[2]),
                list[1]);
        }
    });

    httpPOSTList.forEach(list => {
        if (list[2] === sessionUtil.sConstant.LEVEL_NONE) {
            app.post('/api' + list[0], list[1]);
        } else {
            app.post('/api' + list[0],
                sessionUtil.apiReadSession(list[2]),
                list[1]);
        }
    });
}

/* Get Handlers */
function getCheerInfo(req, res) {

    let recvData = JSON.parse(JSON.stringify(req.query));

    cheerDBUtil.getCheerInfo(recvData.index, (result, info, msg) => {

        if (result == false) {
            return res.send({ r: false, d: null, m: msg });
        }

        res.send({ r: true, d: info, m: msg });
    });
}

function getCheerInfoList(req, res) {

    cheerDBUtil.getCheerInfoList((result, infoList, msg) => {
        if (result == false) {
            return res.send({ r: false, d: null, m: msg });
        }

        res.send({ r: true, d: infoList, m: msg });
    });
}

/* Post Handlers */
function adminLogIn(req, res) {

    let recvData = JSON.parse(JSON.stringify(req.body));
    let userID = recvData.mName;
    let password = recvData.mPassWord;

    if (userID.toLowerCase() !== 'admin') {
        return res.send({ r: false, d: null, m: '사용자 정보가 일치하지 않습니다.' });
    }

    userDBUtil.checkPassWord('admin', password, (result, msg) => {
        if (result === false) {
            return res.send({ r: false, d: null, m: '사용자 정보가 일치하지 않습니다.' });
        }

        req.session.userInfo = {};
        req.session.userInfo.mName = 'admin';

        res.send({ r: true, d: '/home', m: '' });
    });
}

function adminLogOut(req, res) {
    req.session.destroy();

    res.send({ r: true, d: '/', m: '' });
}

function saveQRCodeInfo(req, res) {

    let recvData = JSON.parse(JSON.stringify(req.body));

    let qrList = recvData.qrList;
    
    cheerDBUtil.updateCheerInfo(qrList, (result, msg) => {
        if (result === false) {
            return res.send({ r: false, d: null, m: msg });
        }

        webSocketAPI.updateCheerData(true);
        res.send({ r: true, d: null, m: msg });
    });
}