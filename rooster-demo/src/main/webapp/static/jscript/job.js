/**
 * Created by Administrator on 2019/12/28.
 */
var myChart = echarts.init(document.getElementById('main'));
var data = [];
var now = +new Date(1997, 9, 3);
var oneDay = 24 * 3600 * 1000;
var value = Math.random() * 1000;

function randomData() {
    now = new Date(+now + oneDay);
    value = value + Math.random() * 500 - 10;
    return {
        name: now.toString(),
        value: [
            [now.getFullYear(), now.getMonth() + 1, now.getDate()].join('/'),
            Math.round(value)
        ]
    }
}

option = {
    title: {
        text: '定时任务监控',
        left: 'center'
    },
    tooltip: {
        trigger: 'axis',
        formatter: function (params) {
            params = params[0];
            return params.name + '<br/>任务耗时：' + params.value[1] + "ms";
        },
        axisPointer: {
            animation: false
        }
    },
    xAxis: {
        type: 'time',
        splitLine: {
            show: false
        }
    },
    yAxis: {
        name: '执行耗时ms',
        type: 'value',
        boundaryGap: [0, '100%'],
        splitLine: {
            show: false
        }
    },
    series: [{
        name: '模拟数据',
        type: 'line',
        showSymbol: true,
        symbol: 'emptyCircle',
        symbolSize: 6,
        hoverAnimation: false,
        itemStyle:{
            normal:{
                color:'#ff0000',
                borderWidth: 1,
                borderType: 'solid'
            }
        },
        data: data
    }]
};

myChart.setOption(option);

/**
 * 刷新数据
 * @param jsonObj
 */
function refreshChart(jsonObj) {
    var seriesData = {name: '任务名称：' + jsonObj.code + '<br/>触发时间：' + jsonObj.fireTime, value:[jsonObj.fireTime, jsonObj.runTime]};
    if(data.length>30){
        data.shift();
    }
    data.push(seriesData);
    myChart.setOption({
        series: [{
            data: data
        }]
    });
}


var monitor_websocket;

/**
 * 初始化WebSocket
 */
function init_monitorWebsocket(wsUrl) {
    monitor_websocket = new WebSocket(wsUrl);
    monitor_websocket.onopen = function (evt) {
        chartWriteToScreen("Connectiond to server success!");
    };
    monitor_websocket.onmessage = function (evt) {
        var jsonObj = $.parseJSON(evt.data);
        refreshChart(jsonObj);
    };
    monitor_websocket.onerror = function (evt) {
        chartWriteToScreen("<span style='color:red'>Error:</span>" + evt.data);
        monitor_websocket.close();
    };
}

/**
 * 关闭连接
 */
function close_monitorWebsocket() {
    if(monitor_websocket!=undefined){
        monitor_websocket.close("3000", "关闭连接");;
    }
}

/**
 * 向服务器发送消息
 */
function chartSendMessage(message) {
    monitor_websocket.send(message);
}

/**
 * 写提示信息
 * @param message
 */
function chartWriteToScreen(message) {
    $('#div_monitorMessage').text(message);
}