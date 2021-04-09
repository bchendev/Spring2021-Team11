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

  // Expect the form <pageUrl>.com/crypto.html?cmcId=1
  const cmcId = urlParams.get("cmcId");
  fetch("/get-crypto-history?cmcId=" + cmcId)
    .then((response) => response.json())
    .then((history) => {
      console.log(history);
    });
}

function loadCryptos() {
  fetch("/get-cryptos")
    .then((response) => response.json())
    .then((cryptos) => {
      displayCryptoList(cryptos);
    });
}

function displayCryptoList(cryptos) {
  const cryptoListElement = document.getElementById("crypto-list");
  cryptos.forEach((crypto) => {
    cryptoListElement.appendChild(createCryptoListElement(crypto));
  });
}

/** Creates an element that represents a crypto */
function createCryptoListElement(crypto) {
  const hrefLink = "crypto-brianch.html?cmcId=" + crypto.cmcId;
  
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
