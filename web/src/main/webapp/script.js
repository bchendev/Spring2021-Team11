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
    //   console.log(history);
      drawChart(history);
    });

     fetch("/latest-info?cmcId="+cmcId)
    .then((response) => response.json())
    .then((infos) => {
        console.log(infos);

        const container = document.getElementById("cryptoLatestInfo");

        infos.forEach((info) => {
        container.appendChild(createLatestInfo(info));
      });

    });
}

function createLatestInfo(info){
    const cryptoContainer = document.createElement("span");

    const cryptoName = document.createElement('span');
    cryptoName.innerHTML = info.name;
    cryptoName.className='cryptoName';

    const cryptoSymbol = document.createElement('span');
    cryptoSymbol.innerHTML = info.symbol;
    cryptoSymbol.className='cryptoSymbol';

    const cryptoPrice = document.createElement('span');
    cryptoPrice.innerHTML = info.usd;
    cryptoPrice.className='cryptoPrice';

    const cryptoRank = document.createElement('span');
    cryptoRank.innerHTML = '#'+info.cmcRank;
    cryptoRank.className='cryptoRank';

    cryptoContainer.appendChild(cryptoRank);
    cryptoContainer.appendChild(cryptoName);
    cryptoContainer.appendChild(cryptoSymbol);
    cryptoContainer.appendChild(cryptoPrice);

return cryptoContainer;
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

  fetch('/save-crypto', {
    method: 'POST',
  });

  // Populate the stocks
  fetch('/get-cryptos')
    .then((response) => response.json())
    .then((cryptos) => {
      const stockListElement = document.getElementById('stock-list');
      cryptos.forEach((stock) => {
        stockListElement.appendChild(createStockElement(stock));
      });
    });

}

/** Creates an element that represents a stock */
var count = 0;
function createStockElement(stock) {
  const hrefLink = "ticker.html?cmcId=" + stock.cmcId;

  const stockElement = document.createElement('tr');

  const titleElement = document.createElement('td');
  var ticker = stock.symbol;

  
    const tickName = document.createElement("a");
  tickName.setAttribute('href', hrefLink);
  tickName.className = 'tickName';
  tickName.innerHTML = stock.name;   


  const tickLink = document.createElement('a');
  tickLink.setAttribute('href', hrefLink);
  tickLink.className = 'tickLink';
  tickLink.innerHTML = ticker;

  const counterElement = document.createElement('td');
  counterElement.className = 'tickCount';
  counterElement.innerHTML = stock.cmcRank;

  const priceElement = document.createElement('td');
  priceElement.className = 'price-container';

  const realPrice = document.createElement('a');  
  realPrice.innerText = '$' + stock.usd;
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
    // title: stockData.data.symbol,
    width: 1500,
    height: 500,
    lineWidth: 2,
    backgroundColor: { fill:'transparent' },

    hAxis: {
      lable: 'Time',
      logScale: false,
      gridlines: { count: 1 },
    textStyle:{color: '#FFF'},
    },
    vAxis: {
      lable: 'Price',
      logScale: false,
      format: 'currency',
      gridlines: { count: 1 },
      textStyle:{color: '#FFF'},
    },
    colors: ['#00FF00'],
  };

  const chart = new google.visualization.LineChart(
    document.getElementById('curve_chart')
  );
  chart.draw(data, options);
}

google.charts.load('current', {packages: ['corechart', 'bar']});
google.charts.setOnLoadCallback(barChart);

function barChart() {
  var arrStocks = refreshBarChart();

  
      var data = google.visualization.arrayToDataTable([
        ['Stock', 'Mentions'],
        ['Stock 1', 2695000],
        ['Stock 2', 2695000],
        [arrStocks[0], parseInt(arrStocks[1])],
        ['Stock 3', 2695000],
        ['Stock 4', 2099000],
        ['Stock 5', 1526000]
      ]);

      var options = {
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
      };

      var chart = new google.visualization.BarChart(document.getElementById('bar_chart'));

      chart.draw(data, options);
    }

async function refreshComments() {
  const responseFromServer = await fetch('/refreshComment');
  var stringComments = await responseFromServer.json();
  const comments = stringComments.replaceAll('?','').replaceAll('|','\n')
  const commentsContainer = document.getElementById('comments-container');
  commentsContainer.innerText = comments;
}

async function refreshBarChart(){
  const responseFromServer = await fetch('/barChart');
  var countStocks = await responseFromServer.json();
  var stickerCount = countStocks.split(',');
  return stickerCount;  
  //const comments = stringComments.replaceAll('?','').replaceAll('|','\n')
  // const commentsContainer = document.getElementById('comments-container');
  // commentsContainer.innerText = comments;
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