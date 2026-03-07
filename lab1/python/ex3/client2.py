import socket;

serverIP = "127.0.0.1"
serverPort = 9010
msg = (300).to_bytes(4, byteorder='little')

print('PYTHON UDP CLIENT')
client = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
client.sendto(bytes(msg), (serverIP, serverPort))
data, server = client.recvfrom(1024)
print(f"Client received {int.from_bytes(data, byteorder='little')}")
