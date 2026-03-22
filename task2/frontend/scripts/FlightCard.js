class FlightCard extends HTMLElement {
    constructor() {
        super();
        console.log("FlightCard");
    }

    set info(data){
        this.innerHTML = `
        <div class="flight-card">
                        <div class="card-header">
                            <div class="airline-info">
                                <span class="airline-name">${data?.airline?.name ?? "---"}</span>
                                <span class="flight-number">${data?.number ?? "---"}</span>
                            </div>
                            <div class="card-header-right">
                                ${data.status.toLowerCase() === 'departed' ?
            `<div class="find-flight badge" onclick="track('${data.number}')">Track</div>` : ''}
                                <div class="badge status-departed">${data.status}</div>
                            </div>
                           </div>

                        <div class="card-body">
                            <div class="airport">
                                <span class="iata">${data.movement?.departureAirport?.iata ?? "---"}</span>
                                <span class="icao">${data.movement?.departureAirport?.icao ?? "---"}</span>
                                <span class="city">${data.movement?.departureAirport?.name ?? "---"}</span>
                            </div>


                            <div class="airport text-right">
                                <span class="iata">${data.movement?.airport?.iata ?? "---"}</span>
                                <span class="icao">${data.movement?.airport?.icao ?? "---"}</span>
                                <span class="city">${data.movement?.airport?.name ?? "---"}</span>
                            </div>
                        </div>

                        <div class="card-footer">
                            <div class="time-info">
                                <div class="time-label">Scheduled: ${data.movement.scheduledTime?.local ?? "---"}</div>
                                <div class="time-revised">Revised: <strong>${data.movement.revisedTime?.local ?? "---"}</strong></div>
                            </div>
                            <div class="aircraft-info">
                                <span class="model">${data?.airline?.model ?? "---"}</span>
                            </div>
                        </div>
                    </div>`
    }
}
customElements.define('flight-card', FlightCard);