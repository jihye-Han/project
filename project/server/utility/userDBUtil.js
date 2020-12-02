const crypto = require('crypto');
var async = require('async');

module.exports.createUserTable = createUserTable;

module.exports.checkPassWord = checkPassWord;
module.exports.getUserInfo = getUserInfo;

const userTableName = 'user_tb';

function createUserTable(next) {

    let sql = 'CREATE TABLE IF NOT EXISTS ' + userTableName + ' (\
        `id` INT NOT NULL AUTO_INCREMENT,\
        `CARROT_ACCOUNT` VARCHAR(255) NOT NULL,\
        `CARROT_PASSWORD` VARCHAR(255) NOT NULL,\
        PRIMARY KEY (`id`),\
        UNIQUE KEY `CARROT_ACCOUNT` (`CARROT_ACCOUNT`)) DEFAULT CHARSET=utf8;'

    global.db.query(sql, (err, results, fields) => {
        if (err) {
            console.log(err);
        }

        /* insert user_tb */
        let userInfo = {};
        userInfo.mCarrotAccount = 'test';
        userInfo.mCarrotPassword = encryptPassword('test');

        sql = 'INSERT IGNORE INTO ' + userTableName + ' (CARROT_ACCOUNT, CARROT_PASSWORD) VALUES (?, ?)';

        global.db.query(sql, [userInfo.mCarrotAccount, userInfo.mCarrotPassword], function (err, results, fields) {
            if (err) {
                console.log(err);
            }
        });

        /* insert user_tb */
        userInfo.mCarrotAccount = 'admin';
        userInfo.mCarrotPassword = encryptPassword('carrot');

        sql = 'INSERT IGNORE INTO ' + userTableName + ' (CARROT_ACCOUNT, CARROT_PASSWORD) VALUES (?, ?)';

        global.db.query(sql, [userInfo.mCarrotAccount, userInfo.mCarrotPassword], function (err, results, fields) {
            if (err) {
                console.log(err);
            }
        });

    });

    next(true);
}

function checkPassWord(userID, password, next) {

    let sql = 'SELECT * FROM ' + userTableName + ' WHERE CARROT_ACCOUNT=?';

    global.db.query(sql, [userID], function (err, result, fields) {
        if (err) {
            return next(false, null, err.code);
        }

        if (result.length === 0) {
            return next(false, null, '사용자 정보가 일치하지 않습니다.');
        }

        let userInfo = {};
        userInfo.mUserID = result[0].CARROT_ACCOUNT;

        let encrypted = encryptPassword(password);

        let sql = 'SELECT STRCMP(?, ?) AS COMPARE';
        let params = [encrypted, result[0].CARROT_PASSWORD];
        global.db.query(sql, params, function (err, result, fields) {
            if (err) {
                return next(false, null, err.code);
            }

            if (result[0].COMPARE !== 0) {
                return next(false, null, '사용자 정보가 일치하지 않습니다.');
            }

            next(true, userInfo, '');
        });
    });
}

function getUserInfo(userID, next) {
    let sql = 'SELECT * FROM ' + userTableName + ' WHERE CARROT_ACCOUNT=?';

    global.db.query(sql, [userID], function (err, result, fields) {
        if (err) {
            return next(false, null, err.code);
        }

        if (result.length === 0) {
            return next(false, null, '사용자 정보가 일치하지 않습니다.');
        }

        let userInfo = {};
        userInfo.mUserID = result[0].CARROT_ACCOUNT;

        next(true, userInfo, '');
    });
}

function encryptPassword(pwd) {
    let encrypted = crypto.createHash('sha256').update(pwd).digest('base64');
    return encrypted;
} 