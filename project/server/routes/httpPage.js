
const sessionUtil = require('../utility/sessionUtil')

module.exports = function (app) {

    /* Web Page Handler */
    let pageList = [

        /* Index Page */
        ['/', index, sessionUtil.sConstant.LEVEL_NONE],
        ['/login', login, sessionUtil.sConstant.LEVEL_NONE],
        ['/home', home, sessionUtil.sConstant.LEVEL_ADMIN],
        ['/insert_person', insert_person, sessionUtil.sConstant.LEVEL_ADMIN],
        ['/update_person', update_person, sessionUtil.sConstant.LEVEL_ADMIN],
        ['/delete_person', delete_person, sessionUtil.sConstant.LEVEL_ADMIN],
    ];

    /* set Web Page Handler */
    pageList.forEach(list => {
        if (list[2] === sessionUtil.sConstant.LEVEL_NONE) {
            app.get(list[0], list[1]);
        } else {
            app.get(list[0],
                sessionUtil.pageReadSession(list[2]),
                list[1]);
        }
    });
}

function index(req, res) {
    res.redirect('/login');
}

function login(req, res) {

    let userInfo = req.session.userInfo;
    if (userInfo === undefined) {
        res.render('login');
    } else {
        return res.redirect('/home');
    }
}

function home(req, res) {
    res.render('home');
}

function insert_person(req, res) {
    res.render('insert');
}

function update_person(req, res) {
    res.render('update');
}

function delete_person(req, res) {
    res.render('delete');
}