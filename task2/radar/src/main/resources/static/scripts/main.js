import * as L from '../lib/leaflet-2.0.0-alpha.1/dist/leaflet.js'

import OpenSkyService from './openSkyService.js';


const scriptURL = import.meta.url;
let assetsURL = scriptURL.substring(0, scriptURL.lastIndexOf('/'));
assetsURL = assetsURL.substring(0, assetsURL.lastIndexOf('/')) + '/assets';
const map = new L.Map('map').setView([52.2297, 21.0122], 10);

new L.TileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors'
}).addTo(map);

const AirplaneIcon = new L.Icon({
    iconUrl: assetsURL + '/plane.png',
    iconSize: [25, 25],
    iconAnchor: [12.5, 12.5],
});

const service = new OpenSkyService();
const markers = new Map();

let lastUpdate = 0;

let selectedIcao = null;

window.setSelectedIcao = (icao) => {
    selectedIcao = icao === selectedIcao ? null : icao;
    updateMarkers();
}

window.focusOnCoordinates = (lat, lng) => {
    if (lat && lng) {
        map.setView([lat, lng], map.getZoom()); // Centers map, keeping current zoom
    }
}

let mapMaxLat = map.getBounds()._northEast.lat;
let mapMaxLon = map.getBounds()._northEast.lng;
let mapMinLat = map.getBounds()._southWest.lat;
let mapMinLon = map.getBounds()._southWest.lng;

const getPopup = (flight) => {
    const p = document.createElement('p');
    p.innerHTML = `<b>Callsign:</b> ${flight.callsign == null || flight.callsign === '' ? 'N/A' : flight.callsign}<br>
                    <b>Icao24:</b> ${flight.icao24 == null || flight.icao24 === '' ? 'N/A' : flight.icao24}<br>
                    <b>Country of origin:</b> ${flight.origin_country == null ? 'N/A' : flight.origin_country}<br>
                    <b>Velocity:</b> ${flight.velocity == null ? 'N/A' : flight.velocity} [m/s]<br>
                    <b>Altitude:</b> ${flight.baro_altitude == null ? 'N/A' : flight.baro_altitude} ft`;    
    return p.innerHTML;
}

const updateMarkers = async () => {
    const flights = await service.fetchBoundedStates(mapMinLat, mapMaxLat, mapMinLon, mapMaxLon);
    let {lat: lat1, lng: lng1} = map.getBounds()._northEast
    mapMaxLat = lat1;
    mapMaxLon = lng1;
    let {lat: lat2, lng: lng2} = map.getBounds()._southWest
    mapMinLat = lat2;
    mapMinLon = lng2;

    let markersToRemove = new Set(markers.keys());
    for (let flight of flights.data) {
        let lat = flight.latitude;
        let lon = flight.longitude;
        if (lat && lon) {        
            if (markers.has(flight.icao24)) {
                markersToRemove.delete(flight.icao24);
                let marker = markers.get(flight.icao24);
                marker.getElement().style.transition = 'none';
                if (marker.getElement().style.transform.includes('rotate')) {
                    marker.getElement().style.transform = marker.getElement().style.transform.replace(/rotate\(\d+deg\)/, `rotate(${flight.true_track}deg)`);
                } else {
                    marker.getElement().style.transform += `rotate(${flight.true_track}deg)`;
                }
                marker.bindPopup(getPopup(flight));
                marker.on('click', (event) => {
                    setSelectedIcao(flight.icao24);
                })

                if (flight.icao24 === selectedIcao) {

                    marker.getElement().style.filter = "hue-rotate(120deg) drop-shadow(0 0 5px red)";
                    marker.openPopup();
                } else {
                    marker.getElement().style.filter = "";
                    marker.closePopup();
                }

                markers.set(flight.icao24, marker);
            }
            if (!markers.has(flight.icao24)) {
                const marker = new L.Marker([lat, lon], { icon: AirplaneIcon }).addTo(map);
                marker.getElement().classList.add('airplane-marker');
                marker.getElement().style.transition = 'none';
                marker.getElement().style.transform += `rotate(${flight.true_track}deg)`;
                
                if (flight.icao24 === selectedIcao) {
                    marker.getElement().style.filter = "hue-rotate(120deg) drop-shadow(0 0 5px red)";
                    marker.setZIndexOffset(1000);
                    marker.openPopup();
                }

                markers.set(flight.icao24, marker);
            }
        }
    }
    for (let marker of markersToRemove) {
        map.removeLayer(markers.get(marker));
        markers.delete(marker);
    }
    lastUpdate = Date.now();
}

map.on('moveend' , async () => {    
    await updateMarkers();
})



setInterval(async () => {
    const timeNow = Date.now();
    if (timeNow - lastUpdate < 1000) {
        await updateMarkers();
    }
}, 5000)

const form = document.querySelector('form');
form.addEventListener('submit' , event => {
    event.preventDefault();
    console.log(form);
})


window.addEventListener("load", function() {
    var now = new Date();
    const offset = now.getTimezoneOffset() * 60000;
    var adjustedDate = new Date(now.getTime() - offset);
    var formattedDate = adjustedDate.toISOString().substring(0,16); // For minute precision
    var datetimeField = document.getElementById("datetime-local");
    datetimeField.value = formattedDate;
});
















