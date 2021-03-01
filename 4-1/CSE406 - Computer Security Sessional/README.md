# Computer Security Sessional

## Offline 1 : Transposition Cipher & DES

Implemanting transposition cipher and DES.

- [Description](TC%20and%20DES/Offline%201/Offline1-documentation.docx)
- [TC Implementation](TC%20and%20DES/tc.cpp)
- [DES Implementation](TC%20and%20DES/des.cpp)

## Project : DNS Cache Poisoning

DNS (Domain Name System) is the Internet’s phonebook; it translates hostnames
to IP address and vice-versa. This is done via DNS resolution. DNS attacks
manipulates this resolution process in various ways. One of them is DNS Cache
Poisoning Attack. There are two main ways to perform this attack, local (where
the attacker and victim DNS server are on the same network, where packet
sniffing is possible) and remote (where packet sniffing is not possible). I've
implemented the remote DNS cache poisoning attack.

### Lab Environment

To demonstrate this attack, I have used three virtual machines, which runs on one
single physical machine.

1. A DNS server
2. Victim user
3. Attacker which also hosts fake DNS server

### Report

More details about the attack [here](DNS%20Cache%20Poisoning/report.pdf).

### Implementation

[C implementation.](DNS%20Cache%20Poisoning/dnsattack.c)

### Video

Watch the full [DNS cache poisoing](https://www.youtube.com/watch?v=-oCMsx-ntHE) video on <img src="https://www.flaticon.com/svg/static/icons/svg/174/174883.svg" height="16" width="16" style="margin-left:5px;">YouTube.
