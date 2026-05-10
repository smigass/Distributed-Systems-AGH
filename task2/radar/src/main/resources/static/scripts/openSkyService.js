export default class OpenSkyService{
    async fetchBoundedStates(minLat, maxLat, minLon, maxLon) {
        return new Promise((resolve, reject) => {
            const flights = 
                fetch(`http://localhost:8080/api/flights/bounded?minLat=${minLat}&maxLat=${maxLat}&minLon=${minLon}&maxLon=${maxLon}`)
                    .then(response => response.json())
                    .then(data => resolve(data))
                    .catch(err => reject(err))
        })
    }
}