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
  stockListItem = stockList.getElementsByTagName('tr');
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

function loadCryptoGraph() {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);

  // Expect the form <pageUrl>.com/crypto.html?cmcId=1
  const cmcId = urlParams.get("cmcId");
  fetch("/get-crypto-history?cmcId=" + cmcId)
    .then((response) => response.json())
    .then((history) => {
      console.log(history);
      drawChart(history);
    });
}

function loadGraph() {
   var location = window.location.href;
   var symbol = location.split("=");
   fetch('/graph-data?symbol=' + symbol[1])
   .then((response) => response.json())
   .then((stocks) => {
      drawChart(stocks); 
    });
  }

function loadStocks() {
  // Activates the doPost request at every refresh and open of page
  fetch('/save-stock', {
    method: 'POST',
  });

//   fetch('/save-crypto', {
//     method: 'POST',
//   });

  // Populate the stocks
  fetch('/get-cryptos')
    .then((response) => response.json())
    .then((cryptos) => {
      displayCryptoList(cryptos);
    });

}

function displayCryptoList(cryptos) {
  const cryptoListElement = document.getElementById("stock-list");
  cryptos.forEach((crypto) => {
    cryptoListElement.appendChild(createCryptoListElement(crypto));
  });
}

function createCryptoListElement(crypto) {
  const hrefLink = "ticker.html?cmcId=" + crypto.cmcId;
  
  const cryptoElement = document.createElement("tr");
  cryptoElement.className = "cryptoRow";
  const cryptoNameAndSymbolContainer = document.createElement("td");
  cryptoNameAndSymbolContainer.className="cryptoNameAndSymbolContainer";

  const cryptoName = document.createElement("a");
  cryptoName.setAttribute("href", hrefLink);
  cryptoName.className = "tickName cryptoName";
  cryptoName.innerHTML = crypto.name;

  const cryptoSymbol = document.createElement("a");
  cryptoSymbol.setAttribute("href", hrefLink);
  cryptoSymbol.className = "tickLink cryptoSymbol";
  cryptoSymbol.innerHTML = crypto.symbol;

  const rankElement = document.createElement("td");
  rankElement.className = "tickPrice cryptoRank";
  rankElement.innerHTML = crypto.cmcRank;

  const priceElement = document.createElement("td");
  priceElement.innerText = "$" + crypto.usd;
  priceElement.className = "tickPrice cryptoPrice";

  cryptoNameAndSymbolContainer.appendChild(cryptoName);
  cryptoNameAndSymbolContainer.appendChild(cryptoSymbol);
  cryptoElement.appendChild(rankElement);
  cryptoElement.appendChild(cryptoNameAndSymbolContainer);
  cryptoElement.appendChild(priceElement);
  return cryptoElement;
}

function refresh() { 
  fetch('/store-comments-urls', {
    method: 'POST',
  });

  fetch('/store-comments', {
    method: 'POST',
  });
  
  fetch('/reddit-count', {
   method: 'POST',
  });

  fetch('/sticker-count');

  refreshComments();

}

/** Creates an element that represents a stock */
var count = 0;
function createStockElement(stock) {

  const stockElement = document.createElement('tr');

  const titleElement = document.createElement('td');
  var ticker = stock.ticker;

  
  const tickName = document.createElement("a");
  tickName.setAttribute('href', 'ticker.html?symbol=' + ticker);
  tickName.className = 'tickName';
  tickName.innerHTML = stock.tickName;

  console.log(stock.tickName);
    


  const tickLink = document.createElement('a');
  tickLink.setAttribute('href', 'ticker.html?symbol=' + ticker);
  tickLink.className = 'tickLink';
  tickLink.innerHTML = ticker;

  const counterElement = document.createElement('td');
  counterElement.className = 'tickCount';
  count = count + 1;
  counterElement.innerHTML = count;

  const priceElement = document.createElement('td');
  priceElement.className = 'price-container';

  const realPrice = document.createElement('a');  
  realPrice.innerText = '$' + stock.price;
  realPrice.className = 'tickPrice';


  priceElement.appendChild(realPrice);
  titleElement.appendChild(tickName);
  titleElement.appendChild(tickLink);
  stockElement.appendChild(counterElement);
  stockElement.appendChild(titleElement);
  stockElement.appendChild(priceElement);
  return stockElement;
}

google.charts.load('current', { packages: ['corechart', 'line'] });

/** Creates a chart and adds it to the page. */
function drawChart(stockData) { 

  var my2d = [];
  for (var i = 0; i < stockData.data.quotes.length; i++) {
    my2d[i] = [];
    for (var j = 0; j < 2; j++) {
      let currentTime = new Date();
      let oldTime = new Date(stockData.data.quotes[i].quote.USD.timestamp);

      let days = (currentTime - oldTime) / (1000 * 60 * 60 * 24);

       my2d[i][j] = days;
    }
  }

  for (var i = 0; i < stockData.data.quotes.length; i++) {
    my2d[i][1] = stockData.data.quotes[i].quote.USD.open;
  }

  const data = new google.visualization.DataTable();

  data.addColumn('number', 'Days Past');
  data.addColumn('number', 'Price');

  data.addRows(my2d);

  const options = {
    title: stockData.data.symbol,
    width: 1500,
    height: 500,
    lineWidth: 2,
    backgroundColor: { fill:'transparent' },

    hAxis: {
      lable: 'Time',
      logScale: false,
      gridlines: { count: 1 },
    },
    vAxis: {
      lable: 'Price',
      logScale: false,
      format: 'currency',
      gridlines: { count: 0 },
    },
    colors: ['#00FF00'],
  };

  const chart = new google.visualization.LineChart(
    document.getElementById('curve_chart')
  );
  chart.draw(data, options);
}

google.charts.load('current', {packages: ['corechart', 'bar']});

function barChart() {
  fetch("/get-reddit-mentions")
    .then((response) => response.json())
    .then((stockCounts) => {
      console.log(stockCounts);
      var stockCountArray = [['Stock', 'Mentions']];
      stockCounts.forEach(element => {
        const stock = element.ticker;
        const count = element.count;
        stockCountArray.push([stock, count]);
      });

      const data = google.visualization.arrayToDataTable(stockCountArray);

      const options = {
        title: 'Reddit: wallstreetbets Stock Mentions',
        chartArea: {width: '60%'},
        width: 680,
        height: 300,
        backgroundColor: { fill:'transparent' },

        hAxis: {
          title: 'Mentions',
          minValue: 0
        },
        vAxis: {
          title: 'Stocks'
        }
      }

      const chart = new google.visualization.BarChart(document.getElementById('bar_chart'));
      chart.draw(data, options);
    })
  }

async function refreshComments() {
  const responseFromServer = await fetch('/refreshComment');
  var stringComments = await responseFromServer.json();
  const comments = stringComments.replaceAll('?','').replaceAll('|','\n')
  const commentsContainer = document.getElementById('comments-container');
  commentsContainer.innerText = comments;
}


var i = 0;
var txt = 'This is Bat$ Finance';
var speed = 300;

function typeWriter() {
  if (i < txt.length) {
    document.getElementById("title").innerHTML += txt.charAt(i);
    i++;
    setTimeout(typeWriter, speed);
  }
}