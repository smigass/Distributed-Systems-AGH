const list = document.getElementById("flights-list");


const findDepartures = () => {
    const originIcaoInput = document.querySelector("#origin");
    const destinationIcaoInput = document.querySelector("#destination");
    const departureTime = document.querySelector("#datetime-local");

    const fetchUrl = `http://localhost:8080/api/airport/departures?airportIcao=${originIcaoInput.value}&fromLocal=${departureTime.value}&${destinationIcaoInput.value === '' ? '' : "destinationIcao=" + destinationIcaoInput.value}`;

    list.innerHTML = `<h2>Flights</h2>`;


    fetch(fetchUrl)
        .then(response => response.json())
        .then(data => {
            data.data.forEach(departure => {
                const card = document.createElement("flight-card");
                card.info = departure;

                list.appendChild(card);
            })
        })
}

const track = (iata) => {
    fetch(`http://localhost:8080/api/flights/track?flightIata=${iata}`)
        .then(res => res.json())
        .then(data => {
            if (data?.status !== 200) {
                alert(data?.message);
            }
            console.log(data);
            if (window.setSelectedIcao) {
                window.setSelectedIcao(data?.data?.icao24);
            }

            if (window.focusOnCoordinates && data?.data?.latitude && data?.data?.longitude) {
                window.focusOnCoordinates(data.data.latitude, data.data.longitude);
            }

            window.scrollTo({ top: 0, behavior: 'smooth' });
        })
}




