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

var GREET_TIME = 4000;
var greetTime = setInterval(addRandomGreeting, GREET_TIME);

function addRandomGreeting(){
    const greetings =
        ['Jsoup Library was used for web scraping📚.', 'This is Google SPS Team 11 Final Project.',
             'CoinMarketCap.com was scraped for Cryptocurrency info💹.', 'Reddit.com was scraped for the trending comments📈.',
                 'Reddit.com was scraped for the highest stock mentions📈.', 'It was a very fun project in the making🤪.'];
    
    const load = ['Collecting info from info database...', 'Loading...', 'Almost there :))', '...'];

    // Pick a random greeting.
    const greeting = greetings[Math.floor(Math.random() * greetings.length)];

    const loading = load[Math.floor(Math.random() * load.length)];

    // Add it to the page.
    const greetingContainer = document.getElementById('greeting-container');
    greetingContainer.innerText = greeting;
}
var LOAD_TIME = 1000;
var loadTime = setInterval(addLoadingGreeting, LOAD_TIME);
let c = 0;
function addLoadingGreeting(){
    
    const load = ['', '.', '..', '...'];

    const loadingContainer = document.getElementById('loading-container');

    // Pick a random greeting.
    const loading = load[c];

    // Add it to the page.
    loadingContainer.innerText = loading;
    if(c != 3){
    c=c+1;
    }else{
        c = 0;
    }
}



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

    const formatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
})
    const cryptoPrice = document.createElement('span');
    cryptoPrice.innerHTML = formatter.format(info.usd);

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

  const formatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
 })

  const realPrice = document.createElement('a');  
  realPrice.innerText = formatter.format(stock.usd);
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
        var days = new Date(stockData.data.quotes[i].quote.USD.timestamp); // Days you want to subtract
        var day = days.getDate();
        var month = days.getMonth() + 1;
        var year= days.getFullYear();
        my2d[i][j] = month + "/" + day + "/" + year;

    }
  }

  for (var i = 0; i < stockData.data.quotes.length; i++) {
    my2d[i][1] = stockData.data.quotes[i].quote.USD.close;
  }

  const data = new google.visualization.DataTable();


  data.addColumn('string', 'Days Past');

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
    textStyle:{color: '#FFF', fontName: 'Josefin Sans'},
    },
    vAxis: {
      lable: 'Price',
      logScale: false,
      format: 'currency',
      gridlines: { count: 0 },
      textStyle:{color: '#FFF', fontName: 'Josefin Sans'},
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
          minValue: 0,
         textStyle:{color: '#FFF', fontName: 'Josefin Sans'},

        },
        vAxis: {
          title: 'Stocks',
        textStyle:{color: '#FFF', fontName: 'Josefin Sans'},

        }
      }

      const chart = new google.visualization.BarChart(document.getElementById('bar_chart'));
      chart.draw(data, options);
    })
  }

async function userRefresh(){

  refreshComments();

  fetch('/reddit-count', {
   method: 'POST',
  });

  fetch('/sticker-count');
}

async function refreshComments() {
  const responseFromServer = await fetch('/refreshComment');
  var stringComments = await responseFromServer.json();
  const comments = stringComments.replaceAll('?','').replaceAll('|','\n\n')
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
var myVar;

    function myFunction() {
        myVar = setTimeout(showPage, 8500);
    }

    function showPage() {
        document.getElementById("preload").style.display = "none";
        document.getElementById("myDiv").style.display = "block";
    }