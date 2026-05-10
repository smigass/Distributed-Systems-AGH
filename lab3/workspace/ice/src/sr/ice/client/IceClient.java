package sr.ice.client;

import sr.ice.Demo.A;
import sr.ice.Demo.CalcPrx;
import com.zeroc.Ice.*;
import sr.ice.Demo.EmptyInput;

import java.io.IOException;
import java.lang.Exception;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class IceClient {
	public static void main(String[] args) {
		int status = 0;
		Communicator communicator = null;
		com.zeroc.Ice.InitializationData initData = new com.zeroc.Ice.InitializationData();
		initData.properties = com.zeroc.Ice.Util.createProperties();
		try {
			initData.properties.setProperty("Ice.Package.Demo", "sr.ice");
			// 1. Inicjalizacja ICE
			communicator = Util.initialize(args, initData);

			// 2. Uzyskanie referencji obiektu na podstawie linii w pliku konfiguracyjnym (wówczas aplikację należy uruchomić z argumentem --Ice.config=config.client)
			ObjectPrx base1 = communicator.propertyToProxy("Calc1.Proxy");
            if(base1 == null) { //powyższa opcja się nie uda, gdy nie był wskazany plik konfiguracyjny (--Ice.Config=client.config)
                // 2. Uzyskanie referencji obiektu - to samo co powyżej, ale mniej ładnie
                System.out.println("(using a hard-coded configuration)");
                base1 = communicator.stringToProxy("calc/calc33:tcp -h 127.0.0.1 -p 10011 -z : udp -h 127.0.0.1 -p 10011 -z"); //opcja -z włącza możliwość kompresji wiadomości
            }

//			CalcPrx obj2 = CalcPrx.checkedCast(communicator.stringToProxy("calc/calc22:tcp -h 127.0.0.1 -p 10011 -z : udp -h 127.0.0.1 -p 10011 -z"));

			// 3. Rzutowanie, zawężanie (do typu Calc)
			CalcPrx obj1 = CalcPrx.checkedCast(base1);
			//CalcPrx obj1 = CalcPrx.uncheckedCast(base1); //na czym polega różnica?
			if (obj1 == null) throw new Error("Invalid proxy");

			CompletableFuture<Long> cfl = null;
			String line = null;
			java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
			long r, rs;
			A a;
			double res;

			// 4. Wywołanie zdalnych operacji i zmiana trybu działania proxy obiektu obj1
			do {
				try {
					System.out.print("==> ");
					line = in.readLine();
					switch (line) {
						case "avg":
							try {
								res = obj1.avg(new long[]{});
								System.out.println("RESULT = " + res);
							} catch (EmptyInput e) {
								System.out.println("Caught EmptyInput exception: " + e.reason);
							}
							break;
						case "add":
							r = obj1.add(7, 8);
//							rs = obj2.add(1, 4);
							System.out.println("RESULT = " + r);
//							System.out.println("RESULT2 = " + rs);
							break;
						case "add2":
							r = obj1.add(7000, 8000);
							System.out.println("RESULT = " + r);
							break;
						case "subtract":
							r = obj1.subtract(7, 8);
							System.out.println("RESULT = " + r);
							break;
						case "op":
							a = new A((short) 11, 22, 33.0f, "ala ma kota");
							obj1.op(a, (short) 44);
							System.out.println("DONE");
							break;
						case "op2":
							a = new A((short) 11, 22, 33.0f, "ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ");
							obj1.op(a, (short) 44);
							System.out.println("DONE");
							break;
						case "op 10":
							a = new A((short) 11, 22, 33.0f, "ala ma kota");
							for (int i = 0; i < 10; i++) obj1.op(a, (short) 44);
							System.out.println("DONE");
							break;
						case "add-with-ctx": //wysłanie dodatkowych danych stanowiących kontekst wywołania
							Map<String, String> map = new HashMap<>();
							map.put("key1", "val1");
							map.put("key2", "val2");
							r = obj1.add(7, 8, map);
							System.out.println("RESULT = " + r);
							break;

						/* PONIŻEJ WYWOŁANIA REALIZOWANE W TRYBIE ASYNCHRONICZNYM (completable future) */

						case "add-asyn1":
							obj1.addAsync(7000, 8000).whenComplete((result, ex) -> System.out.println("RESULT (asyn) = " + result));
							break;
						case "add-asyn2-req":  // 1. wysłanie żądania
							cfl = obj1.addAsync(7000, 8000);
							break;
						case "add-asyn2-res":  // 2. odebranie wyniku
							r = cfl.join();
							System.out.println("RESULT = " + r);
							break;
						case "op-asyn1a 100": //co się dzieje "w sieci"? dlaczego "działa" tak wolno?
							a = new A((short) 11, 22, 33.0f, "ala ma kota");
							for (int i = 0; i < 100; i++) {
								obj1.opAsync(a, (short) 99);
							}
							System.out.println("DONE");
							break;
						case "op-asyn1b 100":
							a = new A((short) 11, 22, 33.0f, "ala ma kota");
							for (int i = 0; i < 100; i++) {
								obj1.opAsync(a, (short) 99).whenComplete((result, ex) ->
										System.out.println("CALL (asyn) finished")
								);
							}
							System.out.println("DONE");
							break;

						/* PONIŻEJ USTAWIANIE TRYBU PRACY PROXY */

						case "compress on":
							obj1 = obj1.ice_compress(true);
							System.out.println("Compression enabled for obj1");
							break;
						case "compress off":
							obj1 = obj1.ice_compress(false);
							System.out.println("Compression disabled for obj1");
							break;

						case "set-proxy twoway":
							obj1 = obj1.ice_twoway();
							System.out.println("obj1 proxy set to 'twoway' mode");
							break;
						case "set-proxy oneway":
							obj1 = obj1.ice_oneway();
							System.out.println("obj1 proxy set to 'oneway' mode");
							break;
						case "set-proxy datagram":
							obj1 = obj1.ice_datagram();
							System.out.println("obj1 proxy set to 'datagram' mode");
							break;
						case "set-proxy batch oneway":
							obj1 = obj1.ice_batchOneway();
							System.out.println("obj1 proxy set to 'batch oneway' mode");
							break;
						case "set-proxy batch datagram":
							obj1 = obj1.ice_batchDatagram();
							System.out.println("obj1 proxy set to 'batch datagram' mode");
							break;
						case "flush": //sensowne tylko dla operacji wywoływanych w trybie batch
							obj1.ice_flushBatchRequests();
							System.out.println("Flush DONE");
							break;
						case "x":
						case "":
							break;
						default:
							System.out.println("???");
					}
				} catch (IOException | TwowayOnlyException ex) {
					ex.printStackTrace(System.err);
				}
			}
			while (!Objects.equals(line, "x"));


		} catch (LocalException e) {
			e.printStackTrace();
			status = 1;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			status = 1;
		}
		if (communicator != null) { //clean
			try {
				communicator.destroy();
			} catch (Exception e) {
				System.err.println(e.getMessage());
				status = 1;
			}
		}
		System.exit(status);
	}

}