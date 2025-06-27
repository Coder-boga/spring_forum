/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 7;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 99.79522184300342, "KoPercent": 0.20477815699658702};
    var dataset = [
        {
            "label" : "FAIL",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "PASS",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.8921501706484641, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.9906542056074766, 500, 1500, "获取板块信息"], "isController": false}, {"data": [0.9719626168224299, 500, 1500, "点赞帖子"], "isController": false}, {"data": [0.26635514018691586, 500, 1500, "获取回复列表"], "isController": false}, {"data": [0.9906542056074766, 500, 1500, "获取贴子详情"], "isController": false}, {"data": [0.7772727272727272, 500, 1500, "用户登录"], "isController": false}, {"data": [0.9579439252336449, 500, 1500, "获取用户所有帖子"], "isController": false}, {"data": [0.989247311827957, 500, 1500, "获取未读数"], "isController": false}, {"data": [0.9768518518518519, 500, 1500, "获取首页板块列表"], "isController": false}, {"data": [0.9555555555555556, 500, 1500, "用户登出"], "isController": false}, {"data": [0.9863636363636363, 500, 1500, "帖子列表页"], "isController": false}, {"data": [0.8366336633663366, 500, 1500, "获取私信"], "isController": false}, {"data": [0.9863636363636363, 500, 1500, "获取用户信息"], "isController": false}, {"data": [0.9813084112149533, 500, 1500, "回复帖子"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 1465, 3, 0.20477815699658702, 1111.479180887372, 27, 251389, 87.0, 1078.0, 5810.900000000006, 15084.559999999821, 5.6627986981361085, 127.76073515882894, 1.2317878148844634], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["获取板块信息", 107, 0, 0.0, 105.57009345794393, 32, 867, 54.0, 357.0, 387.0, 851.9600000000003, 1.5438778749314634, 0.5442772586428304, 0.28796940831241163], "isController": false}, {"data": ["点赞帖子", 107, 0, 0.0, 122.55140186915884, 38, 690, 66.0, 360.20000000000005, 627.8, 688.72, 1.5434770065201084, 0.33160638811955456, 0.42505909749870174], "isController": false}, {"data": ["获取回复列表", 107, 3, 2.803738317757009, 10420.710280373833, 147, 251389, 7184.0, 16594.8, 20169.999999999993, 237131.1600000003, 0.41447004001378984, 103.49804648915793, 0.07946833565100848], "isController": false}, {"data": ["获取贴子详情", 107, 0, 0.0, 104.08411214953267, 37, 1274, 61.0, 147.4000000000001, 373.5999999999999, 1224.960000000001, 1.5435438034650393, 0.8668346352476162, 0.29695129812755156], "isController": false}, {"data": ["用户登录", 110, 0, 0.0, 680.5545454545459, 59, 5158, 98.5, 3152.4, 3174.6, 4957.800000000001, 1.5859056241980365, 0.45687710853361396, 0.4450503975577053], "isController": false}, {"data": ["获取用户所有帖子", 107, 0, 0.0, 177.6915887850468, 30, 1845, 85.0, 449.40000000000003, 583.0, 1779.3200000000015, 1.543833323714434, 2.139354967139435, 0.3075605449587349], "isController": false}, {"data": ["获取未读数", 93, 0, 0.0, 131.1182795698925, 28, 609, 81.0, 386.2000000000001, 418.19999999999993, 609.0, 1.3613607752437276, 0.2890347101253038, 0.2592435070044208], "isController": false}, {"data": ["获取首页板块列表", 108, 0, 0.0, 141.00000000000003, 27, 2048, 79.5, 355.3, 460.69999999999993, 1960.4299999999967, 1.528748973756476, 2.3423897849842876, 0.2776829190612349], "isController": false}, {"data": ["用户登出", 90, 0, 0.0, 145.85555555555558, 31, 714, 78.5, 419.8, 635.7, 714.0, 1.3151934064970554, 0.2825610834271018, 0.23632381522993964], "isController": false}, {"data": ["帖子列表页", 110, 0, 0.0, 124.17272727272724, 30, 880, 79.5, 358.6, 404.1499999999997, 859.7600000000001, 1.5824378173866758, 6.088297445262037, 0.3028884884841684], "isController": false}, {"data": ["获取私信", 202, 0, 0.0, 1324.3118811881186, 29, 69615, 90.0, 3422.100000000004, 5877.799999999998, 38459.259999999966, 2.2346615925835787, 59.06513473308516, 0.40808761505188396], "isController": false}, {"data": ["获取用户信息", 110, 0, 0.0, 423.48181818181814, 28, 36221, 56.5, 157.20000000000005, 379.04999999999967, 32293.78000000002, 1.3757566661663916, 0.5333744106133373, 0.26064139964480465], "isController": false}, {"data": ["回复帖子", 107, 0, 0.0, 145.5981308411215, 36, 1180, 66.0, 397.0, 423.4, 1136.880000000001, 1.5436328750522958, 0.33163987549951673, 0.6301157634490818], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Median
            case 8:
            // Percentile 1
            case 9:
            // Percentile 2
            case 10:
            // Percentile 3
            case 11:
            // Throughput
            case 12:
            // Kbytes/s
            case 13:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["Non HTTP response code: org.apache.http.TruncatedChunkException/Non HTTP response message: Truncated chunk (expected size: 8,192; actual size: 6,872)", 1, 33.333333333333336, 0.06825938566552901], "isController": false}, {"data": ["Non HTTP response code: org.apache.http.TruncatedChunkException/Non HTTP response message: Truncated chunk (expected size: 8,192; actual size: 872)", 1, 33.333333333333336, 0.06825938566552901], "isController": false}, {"data": ["Non HTTP response code: org.apache.http.TruncatedChunkException/Non HTTP response message: Truncated chunk (expected size: 8,192; actual size: 672)", 1, 33.333333333333336, 0.06825938566552901], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 1465, 3, "Non HTTP response code: org.apache.http.TruncatedChunkException/Non HTTP response message: Truncated chunk (expected size: 8,192; actual size: 6,872)", 1, "Non HTTP response code: org.apache.http.TruncatedChunkException/Non HTTP response message: Truncated chunk (expected size: 8,192; actual size: 872)", 1, "Non HTTP response code: org.apache.http.TruncatedChunkException/Non HTTP response message: Truncated chunk (expected size: 8,192; actual size: 672)", 1, "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["获取回复列表", 107, 3, "Non HTTP response code: org.apache.http.TruncatedChunkException/Non HTTP response message: Truncated chunk (expected size: 8,192; actual size: 6,872)", 1, "Non HTTP response code: org.apache.http.TruncatedChunkException/Non HTTP response message: Truncated chunk (expected size: 8,192; actual size: 872)", 1, "Non HTTP response code: org.apache.http.TruncatedChunkException/Non HTTP response message: Truncated chunk (expected size: 8,192; actual size: 672)", 1, "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
