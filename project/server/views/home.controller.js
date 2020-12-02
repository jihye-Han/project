angular
    .module('homeApp', [])
    .controller('HomeController', ['$scope', '$http', HomeController]);

function HomeController($scope, $http) {

    var homeVM = this;
    homeVM.tableX = 0;
    homeVM.tableY = 0;
    homeVM.backupCheerInfoList = [];

    homeVM.onClickSave = () => {

        var table = document.getElementById('tableID');
        if (table == null) {
            return;
        }

        let qrList = [];

        qrList.push({ index: 'x', color: homeVM.tableX });
        qrList.push({ index: 'y', color: homeVM.tableY });

        for (let i = 0; i < table.rows.length; i++) {
            for (let j = 0; j < table.rows[i].cells.length; j++) {

                var content = table.rows[i].cells[j].textContent;

                const contents = content.split('.');

                var info = {};
                info.index = contents[0] + '.' + contents[1];
                if (contents[2] == 'O') {
                    info.color = 'FFAC00';
                } else {
                    info.color = 'FFFFFF';
                }

                qrList.push(info);

                console.log(table.rows[i].cells[j].textContent);
            }
        }

        $http.post('/api/saveQRCodeInfo', {
            qrList: qrList,
        })
            .success(function (data, status, headers, config) {
                alert(data.m);
            })
            .error(function (data, status, header, config) {
                alert(data.m);
            });
    }

    homeVM.onClickQRCode = () => {

        var table = document.getElementById('tableID');
        if (table == null) {
            return;
        }

        initQRTable();

        for (let i = 0; i < table.rows.length; i++) {

            for (let j = 0; j < table.rows[i].cells.length; j++) {

                var qrcode = new QRCode(document.getElementById("qrImage" + (i + 1) + "" + (j + 1)), { width: 80, height: 80 });

                qrcode.makeCode(table.rows[i].cells[j].textContent);

                console.log(table.rows[i].cells[j].textContent);
            }
        }
    }

    homeVM.onClickCreate = () => {

        let tableX = $('#ID_HOME_TABLE_X').val();
        let tableY = $('#ID_HOME_TABLE_Y').val();

        if (tableX === undefined || tableX < 3 || tableX > 10) {
            alert('X 값 범위는  3보다 크고 10 보다 작습니다. ');
            return;
        }

        if (tableY === undefined || tableY < 3 || tableY > 10) {
            alert('Y 값 범위는  3보다 크고 10 보다 작습니다. ');
            return;
        }

        homeVM.tableX = tableX;
        homeVM.tableY = tableY;

        initTable();
    }

    homeVM.onClickLoad = () => {

        $http.get('/api/getCheerInfoList', {
        }).then(function (response) {

            if (response.data.r == true) {

                homeVM.backupCheerInfoList = [];

                for (let i = 0; i < response.data.d.length; i++) {

                    if (response.data.d[i].index == 'x') {
                        homeVM.tableX = response.data.d[i].color;
                    }
                    else if (response.data.d[i].index == 'y') {
                        homeVM.tableY = response.data.d[i].color;
                    } else {

                        let indexs = response.data.d[i].index.split('.');
                        homeVM.backupCheerInfoList.push({
                            x: indexs[0],
                            y: indexs[1],
                            c: response.data.d[i].color
                        });
                    }
                }

                initTable();

                var table = document.getElementById('tableID');
                if (table == null) {
                    return;
                }

                for (let i = 0; i < table.rows.length; i++) {
                    for (let j = 0; j < table.rows[i].cells.length; j++) {

                        let color = '#FFFFFF';
                        for (let z = 0; z < homeVM.backupCheerInfoList.length; z++) {

                            if (homeVM.backupCheerInfoList[z].x == (i + 1) && homeVM.backupCheerInfoList[z].y == (j + 1)) {

                                color = '#' + homeVM.backupCheerInfoList[z].c;
                                break;
                            }
                        }


                        if (color == '#FFFFFF') {

                            table.rows[i].cells[j].style.backgroundColor = "#FFFFFF";
                            table.rows[i].cells[j].textContent = (i + 1) + '.' + (j + 1) + '.' + 'X';

                        } else {

                            table.rows[i].cells[j].style.backgroundColor = "#FFAC00";
                            table.rows[i].cells[j].textContent = (i + 1) + '.' + (j + 1) + '.' + 'O';
                        }
                    }
                }
            }
        }, function (err) {
            console.log(err);
        });
    }

    function initTable() {
        dataTableArea.innerHTML = '';

        var tag = "<p>[Data 표]</p><table id='tableID' class='table table-bordered border='1' style='width:100%'; text-align:center ";
        var b1 = parseInt(homeVM.tableX);
        var b2 = parseInt(homeVM.tableY);
        for (j = 1; j <= b1; j++) {
            tag += "<tr>";
            for (i = 1; i <= b2; i++) {
                tag += "<td id='tdID'>" + j + "." + i + "." + 'X' + "</td>";
            }
            tag += "</tr>";
        }
        tag += "</table>";

        dataTableArea.innerHTML = tag;

        $('td#tdID').click(function () {

            const content = this.textContent.split('.');
            if (content[2] == 'O') {

                this.style.backgroundColor = "#FFFFFF";
                this.textContent = content[0] + '.' + content[1] + '.' + 'X';

            } else {

                this.style.backgroundColor = "#FFAC00";
                this.textContent = content[0] + '.' + content[1] + '.' + 'O';
            }

        });

        alert("Data 표 생성");
    }

    function initQRTable() {

        qrTableArea.innerHTML = '';

        var tag = "<p>[QR Code 표]</p><table id='qrTableID' class='table table-bordered border='1' style='width:100%'; text-align:center ";
        var b1 = parseInt(homeVM.tableX);
        var b2 = parseInt(homeVM.tableY);
        for (j = 1; j <= b1; j++) {
            tag += "<tr>";

            for (i = 1; i <= b2; i++) {
                tag += `<td style="text-align:center"; id='qrID${j}${i}'>` + j + "." + i + "</td>";
                tag += `<td style="text-align:center"; id='qrImage${j}${i}'></td>`;
            }

            tag += "</tr>";
        }
        tag += "</table>";

        qrTableArea.innerHTML = tag;

        alert("QR Code 표");
    }

    // Initialize
    angular.element(document).ready(function () {
    });
}
