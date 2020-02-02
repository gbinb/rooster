<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String ctxPath = request.getContextPath();
    String host = request.getServerName() + ":" + request.getServerPort();
%>
<html>
<head>
    <title>任务管理</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="http://g.alicdn.com/sj/dpl/1.5.1/css/sui.min.css" rel="stylesheet">
    <script type="text/javascript" src="http://g.alicdn.com/sj/lib/jquery/dist/jquery.min.js"></script>
    <script type="text/javascript" src="http://g.alicdn.com/sj/dpl/1.5.1/js/sui.min.js"></script>
    <script type="text/javascript" src="<%=ctxPath%>/static/jscript/echarts.common.min.js"></script>
    <script type="text/javascript">
        function StringBuilder() {
            this.data = [];
        }

        StringBuilder.prototype.append = function(){
            this.data.push(arguments[0]);
            return this;
        }

        StringBuilder.prototype.toString = function(separate){
            return this.data.join(separate);
        }

        /**
         * 表单转json
         * author Guobingbing
         * description 使用方法 var jsonObj = $('#form').serializeJSON();
         * @returns {{}}
         */
        $.fn.serializeJSON = function()
        {
            var o = {};
            var a = this.serializeArray();
            $.each(a, function() {
                if (o[this.name] !== undefined) {
                    if (!o[this.name].push) {
                        o[this.name] = [o[this.name]];
                    }
                    o[this.name].push(this.value);
                } else {
                    o[this.name] = this.value;
                }
            });
            return o;
        };
    </script>
</head>

<body>
<h2>Hello Rooster</h2>
<p>
</p>
<div class="sui-btn-toolbar">
    <div class="sui-btn-group">
        <button class="sui-btn btn-xlarge btn-primary" onclick="queryRunningTasks()">刷新</button>
    </div>
    <div class="sui-btn-group">
        <button class="sui-btn btn-xlarge btn-primary" onclick="showDialog('add')">新增</button>
    </div>
    <div class="sui-btn-group">
        <button class="sui-btn btn-xlarge btn-primary" onclick="showDialog('modify')">修改</button>
    </div>
    <div class="sui-btn-group">
        <button class="sui-btn btn-xlarge btn-primary" onclick="remove()">删除</button>
    </div>
    <div class="sui-btn-group">
        <button class="sui-btn btn-xlarge btn-primary" onclick="start()">启动</button>
    </div>
    <div class="sui-btn-group">
        <button class="sui-btn btn-xlarge btn-primary" onclick="stop()">停止</button>
    </div>
    <div class="sui-btn-group">
        <button class="sui-btn btn-xlarge btn-primary" onclick="monitor()">应用监控</button>
    </div>
    <div class="sui-btn-group">
        <button class="sui-btn btn-xlarge btn-primary" onclick="stopMonitor()">停止监控</button>
    </div>
</div>
<p></p>
<form id="form_task">
    <table class="sui-table table-primary">
        <thead>
            <tr>
                <th>选择</th>
                <th>任务编号</th>
                <th>名称</th>
                <th>状态</th>
                <th>定时表达式</th>
                <th>最近启动时间</th>
                <th>最近停止时间</th>
                <th>jobClass</th>
                <th>节点IP</th>
                <th>自定义参数</th>
            </tr>
        </thead>
        <tbody id="task_body">
        </tbody>
    </table>
</form>

<div id="taskModal" tabindex="-1" role="dialog" data-hasfoot="false" class="sui-modal hide fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" data-dismiss="modal" aria-hidden="true" class="sui-close">×</button>
                <h4 id="myModalLabel" class="modal-title">任务管理</h4>
            </div>
            <div class="modal-body">
                <form id="form_task_save" method="post" class="sui-form form-horizontal">
                    <input type="hidden" id="hid_action" value="add">
                    <div class="control-group">
                        <label for="inputCode" class="control-label">任务编号：</label>
                        <div class="controls">
                            <input type="text" id="inputCode" name="code" value="printJob1" style="width: 260px; height: 24px;" placeholder="code">
                        </div>
                    </div>
                    <div class="control-group">
                        <label for="inputName" class="control-label">任务名称：</label>
                        <div class="controls">
                            <input type="text" id="inputName" name="name" value="打印任务" style="width: 260px; height: 24px;" placeholder="name">
                        </div>
                    </div>
                    <div class="control-group">
                        <label for="inputExpression" class="control-label">时间表达式：</label>
                        <div class="controls">
                            <input type="text" id="inputExpression" name="expression" value="0/5 * * * * ?" style="width: 260px; height: 24px;" placeholder="expression">
                        </div>
                    </div>
                    <div class="control-group">
                        <label for="inputJobClass" class="control-label">job实现类：</label>
                        <div class="controls">
                            <input type="text" id="inputJobClass" name="jobClass" value="cn.fetosoft.rooster.demo.job.PrintJob" style="width: 260px; height: 24px;" placeholder="jobClass">
                        </div>
                    </div>
                    <div class="control-group">
                        <label for="inputClusterIP" class="control-label">执行节点IP：</label>
                        <div class="controls">
                            <input type="text" id="inputClusterIP" name="clusterIP" value="192.168.1.5" style="width: 260px; height: 24px;" placeholder="clusterIP">
                        </div>
                    </div>
                    <div class="control-group">
                        <label for="inputParams" class="control-label">JSON扩展参数：</label>
                        <div class="controls">
                            <input type="text" id="inputParams" name="params" value='{"env":"dev","weather":"sunny"}' style="width: 260px; height: 24px;" placeholder="params">
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="sui-btn btn-primary btn-large" onclick="save()">保存</button>
                <button type="button" data-dismiss="modal" class="sui-btn btn-default btn-large">取消</button>
            </div>
        </div>
    </div>
</div>
<p>
</p>
<h2>任务监控</h2>
<div id="div_monitorMessage" style="width:1000px;height:30px; font-size: 14px;"></div>
<div id="main" style="width: 1500px;height:450px;"></div>
<script type="text/javascript">
    $(function () {
        queryRunningTasks();
    });

    //查询运行中的任务
    function queryRunningTasks(taskCode) {
        $('#task_body').empty();
        $.get('<%=ctxPath%>/task/fromMysql', function (data) {
            var sb = new StringBuilder();
            $.each(data, function (index, task) {
                sb.append("<tr>");
                sb.append("<td><input id='box_").append(task.code).append("' type=\"checkbox\" value=\"").append(task.code).append("\" />");
                sb.append("<input type='hidden' id='hid_job_").append(task.code).append("' value='").append(task.status).append("' />").append("</td>");
                sb.append("<td>").append(task.code).append("</td>");
                sb.append("<td>").append(task.name).append("</td>");
                sb.append("<td>").append(getTaskStatus(task.status)).append("</td>");
                sb.append("<td>").append(task.expression).append("</td>");
                sb.append("<td>").append(task.start_time).append("</td>");
                sb.append("<td>").append(task.stop_time).append("</td>");
                sb.append("<td>").append(task.jobClass).append("</td>");
                sb.append("<td>").append(task.clusterIP).append("</td>");
                sb.append("<td>").append(task.params).append("</td>");
                sb.append("</tr>");
            });
            $('#task_body').append(sb.toString(''));
            if(taskCode && taskCode!=''){
                $('#box_' + taskCode).prop('checked', true);
            }
        }, 'json');
    }

    function getTaskStatus(action) {
        if(action==1){
            return '运行中';
        }else{
            return '停止';
        }
    }

    function showDialog(action) {
        $('#taskModal').modal('show');
        $('#hid_action').val(action);
        if(action=='modify'){
            var code = getSelectedCheckbox();
            $.get('<%=ctxPath%>/task/fromMysql', {'code':code}, function (data){
                if(data && data.length>0){
                    var task = data[0];
                    $('#inputCode').val(task.code);
                    $('#inputName').val(task.name);
                    $('#inputExpression').val(task.expression);
                    $('#jobClass').val(task.jobClass);
                    $('#inputClusterIP').val(task.clusterIP);
                    $('#inputParams').val(task.params);
                }
            }, 'json');
            $('#inputCode').prop('readonly', true);
        }else{
            $('#inputCode').prop('readonly', false);
        }
    }

    function save() {
        var params = $('#form_task_save').serializeJSON();
        var url = '<%=ctxPath%>/task/add';
        var action = $('#hid_action').val();
        if(action=='modify'){
            url = '<%=ctxPath%>/task/modify';
        }
        $.post(url, params, function (data) {
            $.alert(data.msg);
            if(data.code=='SUCCESS'){
                $('#taskModal').modal('hide');
                queryRunningTasks();
            }
        }, 'json');
    }

    function remove() {
        if(confirm("确定要删除吗?")){
            var code = getSelectedCheckbox();
            $.post('<%=ctxPath%>/task/remove', {code:code}, function (data) {
                $.alert(data.msg);
                if(data.code=='SUCCESS'){
                    setTimeout(queryRunningTasks, 2000);
                }
            }, 'json');
        }
    }

    function start() {
        var code = getSelectedCheckbox();
        $.post('<%=ctxPath%>/task/start', {code:code}, function (data) {
            $.alert(data.msg);
            if(data.code=='SUCCESS'){
                setTimeout(function () {
                    queryRunningTasks(code);
                }, 2000);
            }
        }, 'json');
    }

    /**
     * 停止
     */
    function stop() {
        if(confirm("确定要停止吗?")){
            var code = getSelectedCheckbox();
            $.post('<%=ctxPath%>/task/stop', {code:code}, function (data) {
                $.alert(data.msg);
                if(data.code=='SUCCESS'){
                    setTimeout(function () {
                        queryRunningTasks(code);
                    }, 2000);
                }
            }, 'json');
            closeMonitorWebsocket();
        }
    }

    function getSelectedCheckbox() {
        var taskCode = '';
        var boxs = $('#form_task input[type=checkbox]:checked');
        if(boxs && boxs.length>0){
            taskCode = $(boxs[0]).val();
        }
        return taskCode;
    }

    function monitor() {
        var code = getSelectedCheckbox()
        if(code==''){
            $.alert('请选择需监控的Job!');
            return;
        }
        var status = parseInt($('#hid_job_' + code).val());
        if(status!=1){
            $.alert('请选择运行中的Job!');
            return;
        }
        closeMonitorWebsocket();
        initMonitorWebsocket('ws://<%=host%>/websocket/jobMonitor/' + code, code);
    }

    function stopMonitor() {
        closeMonitorWebsocket();
    }
</script>
<script type="text/javascript" src="<%=ctxPath%>/static/jscript/job.js"></script>
</body>
</html>
