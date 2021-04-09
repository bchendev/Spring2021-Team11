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
  input = document.getElementById("myInput");
  filter = input.value.toUpperCase();
  stockList = document.getElementById("crypto-list");
  stockListItem = stockList.getElementsByTagName("tr");
  for (var i = 0; i < stockListItem.length; i++) {
    item = stockListItem[i];
    txtValue = item.textContent || item.innerText;
    if (txtValue.toUpperCase().indexOf(filter) > -1) {
      stockListItem[i].style.display = "";
    } else {
      stockListItem[i].style.display = "none";
    }
  }
}

function loadCryptoGraph() {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);

  // Expect the form <pageUrl>.com/crypto.html?cmcUrl=bitcoin
  const cmcUrl = urlParams.get("cmcUrl");
  console.log(symbol);
  fetch("/get-crypto-history?cmcUrl=" + cmcUrl)
    .then((response) => response.json())
    .then((history) => {
      console.log(history);
    });
}

function loadCryptos() {
  console.log(loadCryptos);
  fetch("/get-cryptos")
   .then((response) => response.json())
   .then((cryptos) => {
     console.log(cryptos);
     displayCryptoList(cryptos);
   });   
}

function displayCryptoList(cryptos) {
  const cryptoListElement = document.getElementById("crypto-list");
  cryptos.forEach((crypto) => {
    cryptoListElement.appendChild(createCryptoListElement(crypto))
  })
}

/** Creates an element that represents a crypto */
function createCryptoListElement(crypto) {
  console.log("createCryptoListElement");
  console.log(crypto);
  const hrefLink = "crypto-brianch.html?cmcUrl=" + crypto.cmcUrl;
  const cryptoElement = document.createElement('tr');

  const titleElement = document.createElement('td');
  
  const cryptoName = document.createElement("a");
  cryptoName.setAttribute('href', hrefLink);
  cryptoName.className = 'tickName cryptoName';
  cryptoName.innerHTML = crypto.name;

  const cryptoLink = document.createElement('a');
  cryptoLink.setAttribute('href', hrefLink);
  cryptoLink.className = 'tickLink cryptoLink';
  cryptoLink.innerHTML = crypto.symbol;

  const rankElement = document.createElement('td');
  rankElement.className = 'tickPrice cryptoRank';
  rankElement.innerHTML = -1;

  const priceElement = document.createElement('td');
  priceElement.innerText = '$' + crypto.usd;
  priceElement.className = 'tickPrice cryptoPrice';

  titleElement.appendChild(cryptoName);
  titleElement.appendChild(cryptoLink);
  cryptoElement.appendChild(rankElement);
  cryptoElement.appendChild(titleElement);
  cryptoElement.appendChild(priceElement);
  return cryptoElement;
}

var i = 0;
var txt = "This is Bat$ Finance.";
var speed = 300;

function typeWriter() {
  if (i < txt.length) {
    document.getElementById("title").innerHTML += txt.charAt(i);
    i++;
    setTimeout(typeWriter, speed);
  }
}
