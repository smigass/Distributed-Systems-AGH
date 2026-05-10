import type { ConnectRouter, HandlerContext } from "@connectrpc/connect";
import type { ChangeEngineModeRequest, ChangeEngineModeResponse, StreamTelemetryResponse } from "./gen/cardata/v1/cardata_pb.js";
import { EngineMode, ChangeEngineModeResponseSchema, StreamTelemetryResponseSchema } from "./gen/cardata/v1/cardata_pb.js";
import { F1TelemetryService } from "./gen/cardata/v1/cardata_pb.js";
import { create } from "@bufbuild/protobuf";
import { TimestampSchema } from "@bufbuild/protobuf/wkt";

let currentEngineMode: EngineMode = EngineMode.SLOW_IN;

export default (router: ConnectRouter) =>
    router.service(F1TelemetryService, {
        changeEngineMode: async (req: ChangeEngineModeRequest, ctx: HandlerContext): Promise<ChangeEngineModeResponse> => {

            const newMode = req.requestedMode;

            if (newMode === null) {
                return create(ChangeEngineModeResponseSchema, {
                    success: false,
                    message: "No mode provided"
                });
            }

            currentEngineMode = newMode;

            return create(ChangeEngineModeResponseSchema, {
                success: true,
                message: `Engine mode successfully changed to ${EngineMode[newMode]}`
            });
        },
        streamTelemetry: async function* (req: unknown, ctx: HandlerContext): AsyncIterable<StreamTelemetryResponse> {
            while (true) {
                let maxRpm, maxSpeed: number;

                switch (currentEngineMode) {
                    case EngineMode.RACE:
                        maxSpeed = 280;
                        maxRpm = 11000;
                        break;
                    case EngineMode.QUALIFY:
                        maxSpeed = 340;
                        maxRpm = 13000;
                        break;
                    case EngineMode.ATTACK:
                        maxSpeed = 310;
                        maxRpm = 12500;
                        break;
                    case EngineMode.SLOW_IN:
                    default:
                        maxSpeed = 100;
                        maxRpm = 6000;
                        break;
                }

                const now = new Date();
                const response = create(StreamTelemetryResponseSchema, {
                    speed: Math.max(0, Math.random() * maxSpeed),
                    rpm: Math.floor(Math.max(1000, Math.random() * maxRpm)),
                    currentMode: currentEngineMode,
                    tireTemps: {
                        "FL": 60 + Math.random() * 40,
                        "FR": 60 + Math.random() * 40,
                        "RL": 65 + Math.random() * 40,
                        "RR": 65 + Math.random() * 40
                    },
                    time: create(TimestampSchema, {
                        seconds: BigInt(Math.floor(now.getTime() / 1000)),
                        nanos: (now.getTime() % 1000) * 1000000
                    })
                });

                yield response;
                await new Promise((resolve) => setTimeout(resolve, 100));
            }
        },

    });