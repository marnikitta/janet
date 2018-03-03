# Janet (Java Networking)

Janet is a userland network stack implementation. 

The main goals of this project are:
- The deeper understanding of TCP/IP stack.
- Practicing writing Java copy-free garbage-free code

Memory-access patterns were inspired by [Aeron](https://github.com/real-logic/aeron) 
and [Simple Binary Encoding](https://github.com/real-logic/simple-binary-encoding).

#### Roadmap

1. [x] Ethernet
2. [x] ARP
3. [x] IP
4. [x] ICMP
5. [ ] UDP
6. [ ] Performance tuning
7. [ ] Multiple back-off strategies
8. [ ] TCP
9. [ ] Congestion control
10. [ ] Network driver API

#### Getting started

Create a TAP device with:

```bash
sudo ip tuntap add user <username> mode tap name tun2
sudo ip link set tun2 up
sudo addr add 10.0.0.1/24 dev tun2
```

Then run with:

```bash
java -Djava.library.path=target -cp target/janet-1.0-SNAPSHOT.jar marnikitta.janet.Test
```
