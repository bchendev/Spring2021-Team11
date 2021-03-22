// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

google.charts.load('current', { packages: ['corechart', 'line'] });
google.charts.setOnLoadCallback(drawChart);

/** Creates a chart and adds it to the page. */
function drawChart() {
  const data = new google.visualization.DataTable();
  data.addColumn('number', 'Time');
  data.addColumn('number', 'Price');
  data.addRows([
    [0, 0],
    [1, 10],
    [2, 23],
    [3, 17],
    [4, 18],
    [5, 9],
    [6, 11],
    [7, 27],
    [8, 33],
    [9, 40],
    [10, 32],
    [11, 35],
    [12, 30],
    [13, 40],
    [14, 42],
    [15, 47],
    [16, 44],
    [17, 48],
    [18, 52],
    [19, 54],
    [20, 42],
    [21, 55],
    [22, 56],
    [23, 57],
    [24, 60],
    [25, 50],
    [26, 52],
    [27, 51],
    [28, 49],
    [29, 53],
    [30, 55],
    [31, 60],
    [32, 61],
    [33, 59],
    [34, 62],
    [35, 65],
    [36, 62],
    [37, 58],
    [38, 55],
    [39, 61],
    [40, 64],
    [41, 65],
    [42, 63],
    [43, 66],
    [44, 67],
    [45, 69],
    [46, 69],
    [47, 70],
    [48, 72],
    [49, 68],
    [50, 66],
    [51, 65],
    [52, 67],
    [53, 70],
    [54, 71],
    [55, 72],
    [56, 73],
    [57, 75],
    [58, 70],
    [59, 68],
    [60, 64],
    [61, 60],
    [62, 65],
    [63, 67],
    [64, 68],
    [65, 69],
    [66, 70],
    [67, 72],
    [68, 75],
    [69, 80],
  ]);

  const options = {
    title: '*Stock Symbol Here*',
    width: 1500,
    height: 500,

    hAxis: {
      title: 'Time',
      logScale: false,
    },
    vAxis: {
      title: 'Price',
      logScale: false,
      format: 'currency',
    },
    colors: ['#a52714', '#097138'],
  };

  const chart = new google.visualization.LineChart(
    document.getElementById('curve_chart')
  );
  chart.draw(data, options);
}
