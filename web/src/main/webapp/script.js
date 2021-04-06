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


function searchMe() {
  var input, filter, stockList, stockListItem, item, txtValue;
  input = document.getElementById('myInput');
  filter = input.value.toUpperCase();
  stockList = document.getElementById('stock-list');
  stockListItem = stockList.getElementsByTagName('list');
  for (var i = 0; i < stockListItem.length; i++) {
    item = stockListItem[i];
    txtValue = item.textContent || item.innerText;
    if (txtValue.toUpperCase().indexOf(filter) > -1) {
      stockListItem[i].style.display = '';
    } else {
      stockListItem[i].style.display = 'none';
    }
  }
}


function loadStocks() {
    

  // Activates the doPost request at every refresh and open of page
  fetch('/save-stock', {
    method: 'POST',
  });

  // Populate the stocks
  fetch('/list-stock')
    .then((response) => response.json())
    .then((stocks) => {
      const stockListElement = document.getElementById('stock-list');
      stocks.forEach((stock) => {
        stockListElement.appendChild(createStockElement(stock));
      });
    });
}

/** Creates an element that represents a stock */
function createStockElement(stock) {
  const stockElement = document.createElement('list');
  stockElement.className = 'task';


  const titleElement = document.createElement('span');
  var ticker = stock.ticker;

  const tickLink = document.createElement('a');
  tickLink.setAttribute('href', "ticker.html?symbol=" + ticker);
  tickLink.setAttribute('name',ticker);
  tickLink.innerHTML = ticker;

  const priceElement = document.createElement('span');
  priceElement.innerText = '$' + stock.price;

  titleElement.appendChild(tickLink);
  stockElement.appendChild(titleElement);
  stockElement.appendChild(priceElement);
  return stockElement;
}

google.charts.load('current', { packages: ['corechart', 'line'] });
google.charts.setOnLoadCallback(drawChart);

/** Creates a chart and adds it to the page. */
function drawChart() {
  // Feeds graph random data
  function myRand(to) {
    var x = Math.floor(Math.random() * to + 1);
    return x;
  }

  var my2d = [];
  for (var i = 0; i < 70; i++) {
    my2d[i] = [];
    for (var j = 0; j < 2; j++) {
      my2d[i][j] = i;
    }
  }

  for (var i = 0; i < 70; i++) {
    my2d[i][1] = myRand(50);
  }

  const data = new google.visualization.DataTable();

  data.addColumn('number', 'Time');
  data.addColumn('number', 'Price');

  data.addRows(my2d);

  const options = {
    title: 'BTC',
    width: 1500,
    height: 500,
    lineWidth: 3,

    hAxis: {
      lable: 'Time',
      logScale: false,
      gridlines:{count: 0}
    },
    vAxis: {
      lable: 'Price',
      logScale: false,
      format: 'currency',
      gridlines:{count: 0}
    },
    colors: ['#00FF00'],
  };

  const chart = new google.visualization.LineChart(
    document.getElementById('curve_chart')
  );
  chart.draw(data, options);
}
