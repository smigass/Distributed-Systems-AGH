import './App.css'
import {useState, useRef} from "react";
import {createClient} from "@connectrpc/connect";
import {createConnectTransport} from "@connectrpc/connect-web";
import {F1TelemetryService, EngineMode} from "../../server/gen/cardata/v1/cardata_pb";
import type {StreamTelemetryResponse} from "../../server/gen/cardata/v1/cardata_pb";

const transport = createConnectTransport({
    baseUrl: "http://localhost:8080",
    useBinaryFormat: true,
});

const client = createClient(F1TelemetryService, transport);

function App() {
    const [isStreaming, setIsStreaming] = useState(false);
    const [telemetry, setTelemetry] = useState<StreamTelemetryResponse>();
    const abortControllerRef = useRef<AbortController | null>(null);

    const startStream = async () => {
        if (isStreaming) return;

        setIsStreaming(true);
        const controller = new AbortController();
        abortControllerRef.current = controller;

        try {
            for await (const res of client.streamTelemetry({}, {signal: controller.signal})) {
                setTelemetry(res);
                console.log("Received telemetry:", res);
            }
        } catch (err: any) {
            if (err.name === 'AbortError') {
                console.log('Stream stopped');
            } else {
                console.error("Stream error:", err);
            }
        } finally {
            setIsStreaming(false);
        }
    };

    const stopStream = () => {
        if (abortControllerRef.current) {
            abortControllerRef.current.abort();
            abortControllerRef.current = null;
        }
    };

    const changeMode = async (mode: EngineMode) => {
        const res = await client.changeEngineMode({requestedMode: mode});
        if (!res.success) {
            console.log("Failed to change engine mode:", res.message);
        }
    };

    return (
        <div>
            <h1>Telemetry Dashboard</h1>

            <div style={{marginBottom: "20px"}}>
                <h3>Stream Controls</h3>
                <button onClick={startStream} disabled={isStreaming} style={{marginRight: '10px'}}>Start Stream</button>
                <button onClick={stopStream} disabled={!isStreaming}>Stop Stream</button>
            </div>

            <div style={{marginBottom: "20px"}}>
                <h3>Engine Modes</h3>
                <button onClick={() => changeMode(EngineMode.SLOW_IN)} disabled={!isStreaming}
                        style={{marginRight: '10px'}}>SLOW IN
                </button>
                <button onClick={() => changeMode(EngineMode.RACE)} disabled={!isStreaming}
                        style={{marginRight: '10px'}}>RACE
                </button>
                <button onClick={() => changeMode(EngineMode.QUALIFY)} disabled={!isStreaming}
                        style={{marginRight: '10px'}}>QUALIFY
                </button>
                <button onClick={() => changeMode(EngineMode.ATTACK)} disabled={!isStreaming}>ATTACK</button>
            </div>

            <div style={{
                marginTop: '20px',
                textAlign: 'left',
                background: '#222',
                padding: '15px',
                borderRadius: '8px'
            }}>
                <h2>Live Data</h2>
                {isStreaming ? (
                        <div>
                            <h1>Engine mode: {EngineMode[telemetry?.currentMode || 0]}</h1>
                            <h3>Speed: {telemetry?.speed.toFixed(2)} km/h</h3>
                            <h3>RPM: {telemetry?.rpm}</h3>
                            <div style={{display: 'flex', gap: '10px', marginTop: '20px', alignItems: 'center', justifyContent: 'space-between',}}>
                                <div style={{display: 'flex', width: '100%', flexDirection: 'column', justifyContent: 'center', alignItems: 'center'}}>
                                    <div className={'tire'}
                                         style={{background: `rgba(255, 128, 0, ${telemetry?.tireTemps?.FL || 0 / 100})`}}>
                                        {telemetry?.tireTemps?.FL.toFixed(0)}
                                    </div>
                                    <div className={'tire'}
                                         style={{background: `rgba(255, 128, 0, ${telemetry?.tireTemps?.FR || 0 / 100})`}}>
                                        {telemetry?.tireTemps?.FR.toFixed(0)}
                                    </div>

                                </div>
                                <div style={{display: 'flex', width: '100%', flexDirection: 'column', justifyContent: 'center', alignItems: 'center'}}>
                                    <div className={'tire'}
                                         style={{background: `rgba(255, 128, 0, ${telemetry?.tireTemps?.FR || 0 / 100})`}}>
                                        {telemetry?.tireTemps?.FR.toFixed(0)}
                                    </div>
                                    <div className={'tire'}
                                         style={{background: `rgba(255, 128, 0, ${telemetry?.tireTemps?.FR || 0 / 100})`}}>
                                        {telemetry?.tireTemps?.FR.toFixed(0)}
                                    </div>

                                </div>
                            </div>
                        </div>)
                    : (<></>)}
            </div>
        </div>
    )
}

export default App
