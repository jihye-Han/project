var async = require('async');

module.exports.createCheerTable = createCheerTable;
module.exports.getCheerInfo = getCheerInfo;
module.exports.getCheerInfoList = getCheerInfoList;
module.exports.updateCheerInfo = updateCheerInfo;

const cheerTableName = 'cheer_tb';

function createCheerTable(next) {

    let sql = 'CREATE TABLE IF NOT EXISTS ' + cheerTableName + ' (\
        `id` INT NOT NULL AUTO_INCREMENT,\
        `CHEER_INDEX` VARCHAR(255) NOT NULL,\
        `CHEER_COLOR` VARCHAR(255) NOT NULL,\
        PRIMARY KEY (`id`),\
        UNIQUE KEY `CHEER_INDEX` (`CHEER_INDEX`)) DEFAULT CHARSET=utf8;'

    global.db.query(sql, (err, results, fields) => {
        if (err) {
            console.log(err);
        }
    });

    next(true);
}

function getCheerInfo(index, next) {

    let sql = 'SELECT * FROM ' + cheerTableName + ' WHERE CHEER_INDEX = ?';

    global.db.query(sql, [index], (err, results, fields) => {
        if (err) {
            return next(false, null, err.code);
        }

        if (results.length != 1) {
            return next(false, null, null);
        }

        let info = {};
        info.index = results[0].CHEER_INDEX;
        info.color = results[0].CHEER_COLOR;

        next(true, info, '');
    });
}

function getCheerInfoList(next) {

    let infoList = [];

    let sql = 'SELECT * FROM ' + cheerTableName;

    global.db.query(sql, (err, results, fields) => {
        if (err) {
            return next(false, null, err.code);
        }

        for (let i = 0; i < results.length; i++) {
            let info = {};
            info.index = results[i].CHEER_INDEX;
            info.color = results[i].CHEER_COLOR;

            infoList.push(info);
        }

        next(true, infoList, '');
    });
}

function updateCheerInfo(qrList,next) {

    let sql = 'DELETE FROM ' + cheerTableName;

    global.db.query(sql, (err, results, fields) => {
        if (err) {
            return next(false, err.code);
        }

        let index = 0;

        async.whilst(
            function test() { //반복조건

            return index < qrList.length;

        }, function (cb) { //반복실행 함수

            let sql = 'INSERT IGNORE INTO ' + cheerTableName + ' (CHEER_INDEX, CHEER_COLOR) VALUES (?, ?)';
            global.db.query(sql, [qrList[index].index, qrList[index].color], (err, result, fields) => {
                if (err) {
                    return cb(err.code);
                }

                index++;
                cb(null, index);
            });
            

        }, function (err, n) { //최종 콜백함수

            if (err) {
                next(false, '업데이트 실패');
            } else {
                next(true, '업데이트 완료');
            }
        });
    });


}
