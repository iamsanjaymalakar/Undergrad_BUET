# Operating Systems Sessional

## Assignment 1 : Shell Script

- [Description](Assignment%201/Assignement1%20Updated.docx)
- [Script](Shell%20Script/1505057.sh)

## Assignment 2 : IPC

Assignment on interprocess communication.

- [Description](IPC/IPC%20Offline.pdf)
- [Implementation](IPC/procon.cpp)

## Assignment 3 : xv6 Socker API

Implement Socket API (local loopback only) in xv6.

- [Description](xv6%20Socket%20API/Implement%20Socket%20API%20(local%20loopback%20only)%20in%20xv6.txt)
- [Implementation](xv6%20Socket%20API/patch_xv6_Socket_1505057)

## Assignment 4 : xv6 Syscall

1. Create a new system call in xv6 with the following specifications:
    a. Name of the system call will be your firstname_lastname
    b. The system call will print your name and date of birth
    c. The system call will return your student id
2. Create a user program in xv6 that will call your newly added system call
3. From xv6 shell, run the user program
  
- [Implementation](xv6%20SysCall/1505057/)

## Assignment 5 : xv6 Paging

An important feature lacking in xv6 is the ability to swap out pages to a backing store. That is, at each
moment in time all processes are held within the physical memory. You have to implement a paging
framework for xv6 which can take out pages and storing them to disk. Also, the framework will retrieve
pages back to the memory on demand. In your framework, each process is responsible for paging in and
out its own pages. To keep things simple, we will use the file system interface supplied and create for each
process a file in which swapped out memory pages are stored.

- [Description](xv6%20Paging/xv6-assignment-3/xv6-assignment-3.pdf)
- [Implementaion](xv6%20Paging/patch_xv6_Paging_1505057)