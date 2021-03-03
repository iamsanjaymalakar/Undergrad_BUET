# UndergradOfflines

## Level 1 Term 1 

## Level 2 Term 2

# Level 3 Term 1

# Level 3 Term 2

# Operating Systems Sessional

## Assignment 1 : Shell Script

- [Description](3-2/CSE314%20-%20Operating%20System%20Sessional/Shell%20Script/Assignment%201/Assignement1%20Updated.docx)
- [Script](3-2/CSE314%20-%20Operating%20System%20Sessional/Shell%20Script/1505057.sh)

## Assignment 2 : IPC

Assignment on interprocess communication.

- [Description](3-2/CSE314%20-%20Operating%20System%20Sessional/IPC/IPC%20Offline.pdf)
- [Implementation](3-2/CSE314%20-%20Operating%20System%20Sessional/IPC/procon.cpp)

## Assignment 3 : xv6 Socket API

Implement Socket API (local loopback only) in xv6.

- [Description](3-2/CSE314%20-%20Operating%20System%20Sessional/xv6%20Socket%20API/Implement%20Socket%20API%20(local%20loopback%20only)%20in%20xv6.txt)
- [Implementation](3-2/CSE314%20-%20Operating%20System%20Sessional/xv6%20Socket%20API/patch_xv6_Socket_1505057)

## Assignment 4 : xv6 Syscall

1. Create a new system call in xv6 with the following specifications:
    a. Name of the system call will be your firstname_lastname
    b. The system call will print your name and date of birth
    c. The system call will return your student id
2. Create a user program in xv6 that will call your newly added system call
3. From xv6 shell, run the user program
  
- [Implementation](3-2/CSE314%20-%20Operating%20System%20Sessional/xv6%20SysCall/1505057/)

## Assignment 5 : xv6 Paging

An important feature lacking in xv6 is the ability to swap out pages to a backing store. That is, at each
moment in time all processes are held within the physical memory. You have to implement a paging
framework for xv6 which can take out pages and storing them to disk. Also, the framework will retrieve
pages back to the memory on demand. In your framework, each process is responsible for paging in and
out its own pages. To keep things simple, we will use the file system interface supplied and create for each
process a file in which swapped out memory pages are stored.

- [Description](3-2/CSE314%20-%20Operating%20System%20Sessional/xv6%20Paging/xv6-assignment-3/xv6-assignment-3.pdf)
- [Implementaion](3-2/CSE314%20-%20Operating%20System%20Sessional/xv6%20Paging/patch_xv6_Paging_1505057)

# Level 4 Term 1
# Computer Security Sessional CSE406

## Offline 1 : Transposition Cipher & DES

Implemanting transposition cipher and DES.

- [Description](4-1/CSE406%20-%20Computer%20Security%20Sessional/TC%20and%20DES/Offline%201/Offline1-documentation.docx)
- [TC Implementation](4-1/CSE406%20-%20Computer%20Security%20Sessional/TC%20and%20DES/tc.cpp)
- [DES Implementation](4-1/CSE406%20-%20Computer%20Security%20Sessional/TC%20and%20DES/des.cpp)

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

More details about the attack [here](4-1/CSE406%20-%20Computer%20Security%20Sessional/DNS%20Cache%20Poisoning/report.pdf).

### Implementation

[C implementation.](4-1/CSE406%20-%20Computer%20Security%20Sessional/DNS%20Cache%20Poisoning/dnsattack.c)

### Video

Watch the full [DNS cache poisoing](https://www.youtube.com/watch?v=-oCMsx-ntHE) video on <img src="https://www.flaticon.com/svg/static/icons/svg/174/174883.svg" height="16" width="16" style="margin-left:5px;">YouTube.

# Computer Graphics Sessional CSE410

## Assignment 1

There are three tasks each carrying the same weight.

1. Fully Controllable Camera (1.exe)
2. Sphere to/from Cube (1.exe)
3. Wheel (2.exe)

- [Description](4-1/CSE410%20-%20%20Computer%20Graphics%20Sessional/Assignment%201/Read%20Me.pdf)
- [Task 1 and 2 Code](4-1/CSE410%20-%20%20Computer%20Graphics%20Sessional/Assignment%201/1.cpp)
- [Task 3 Code](4-1/CSE410%20-%20%20Computer%20Graphics%20Sessional/Assignment%201/2.cpp)

## Assignment 2

1. Stage 1: Modeling Transformation
2. Stage 2: View Transformation
3. Stage 3: Projection Transformation

- [Description](4-1/CSE410%20-%20%20Computer%20Graphics%20Sessional/Assignment%202/docs/Assignment%202%20Specification.pdf)
- [Implementation](4-1/CSE410%20-%20%20Computer%20Graphics%20Sessional/Assignment%203/1505057.cpp)

## Assignment 3

Rendering a scene using ray casting.

- [Description](4-1/CSE410%20-%20%20Computer%20Graphics%20Sessional/Assignment%203/Assignment%203%20specification.pdf)
- [Implementation](4-1/CSE410%20-%20%20Computer%20Graphics%20Sessional/Assignment%203/1505057.cpp)

# Level 4 Term 2

# Machine Learning

## Assignment 1: Decision Tree and AdaBoost for Classification

Decision  tree  is  one  of  the  models  extensively  used  in  classification  problems.  In  ensemble learning, we combine decisions from multiple weak learners to solve a classification problem. This assignment implements a decision tree classifier and uses it within AdaBoost algorithm.

- [Description](4-2/CSE%20471%20-%20Machine%20Learning%20Sessional/Assignment%201%20-%20Decision%20Tree%20and%20AdaBoost%20for%20Classification/Assignment%201.pdf)
- [Implementation](4-2/CSE%20471%20-%20Machine%20Learning%20Sessional/Assignment%201%20-%20Decision%20Tree%20and%20AdaBoost%20for%20Classification/1505057/1505057.py)
- [Results](4-2/CSE%20471%20-%20Machine%20Learning%20Sessional/Assignment%201%20-%20Decision%20Tree%20and%20AdaBoost%20for%20Classification/1505057/1505057.pdf)

## Assignment 2: Text Classification

Stack Exchange is a very popular Q&A (Question-and-Answer) based website. We want to analyze some archived data of Stack Exchange using text classification.  The link to stack exchange archive is <https://archive.org/details/stackexchange>. The goal of text classification is to identify the topic for a piece of text (newsarticle, web-blog, etc.).  Text classification has obvious utility in the age of infor-mation overload, and it has become very popular for applied machine learningalgorithms.  In this project, you will implement k-nearest neighbor and NaiveBayes,  apply these to text classification on Stack Exchange sample data, and compare the performances of these techniques.

- [Description](4-2/CSE%20471%20-%20Machine%20Learning%20Sessional/Assignment%202%20-%20Text%20Classification/Assignment_2_Version2.pdf)
- [Implementation](4-2/CSE%20471%20-%20Machine%20Learning%20Sessional/Assignment%202%20-%20Text%20Classification/1505057/1505057_Code.py)
- [Results](4-2/CSE%20471%20-%20Machine%20Learning%20Sessional/Assignment%202%20-%20Text%20Classification/1505057/1505057_Report.pdf)

## Assignment 3: Dimensionality  Reduction using Principal  Component Analysis and Clustering  using  Expectation-maximization  Algorithm

Principal  component  analysis  (PCA)  and  the  expectation-maximization  (EM) algorithm  are  two of  the  most  widely used unsupervised methods  in  machinelearning. In this assignment, you will use PCA for dimensionality reductionand  apply the EM algorithm for Gaussian mixture model to cluster  the datawith dimensionality reduced.

- [Description](4-2/CSE%20471%20-%20Machine%20Learning%20Sessional/Assignment%203%20-%20Dimensionality%20Reduction%20using%20PCA%20and%20Clustering%20using%20EM%20Algorithm/CSE472_%20Assignment%203.pdf)
- [Implementation](4-2/CSE%20471%20-%20Machine%20Learning%20Sessional/Assignment%203%20-%20Dimensionality%20Reduction%20using%20PCA%20and%20Clustering%20using%20EM%20Algorithm/1505057/1505057.py)
- [Results](4-2/CSE%20471%20-%20Machine%20Learning%20Sessional/Assignment%203%20-%20Dimensionality%20Reduction%20using%20PCA%20and%20Clustering%20using%20EM%20Algorithm/1505057/1505057.pdf)

## Project : Recomnedation System

The purpose of a recomnedation system is to suggest relevant items to users. This project aims to build a movie recommendation mechanism within Netflix.

- [Notebook](4-2/CSE%20471%20-%20Machine%20Learning%20Sessional/Project%20-%20Recommendation%20System/notebook.ipynb)
- [Notebook as PDF](4-2/CSE%20471%20-%20Machine%20Learning%20Sessional/Project%20-%20Recommendation%20System/notebook.pdf)

