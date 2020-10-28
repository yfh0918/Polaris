Environment

Machine:          MacBook Air 2013
OS:               OS X 10.8.4
Processor:        1.7 GHz Intel Core i7
Memory:           8 GB 1600 MHz DDR3
Network Adapter:  Virtual Loopback Interface


The columns "Avg", "Min", "Max" and "Std Dev" refer to response time in ms.
The column "Error %" indicates the percent of pages that had some error. 
Even if only one image failed to load, the entire page is considered to be in error.

Measurements
Proxy	Avg	Min	Max	Std Dev	Error %	Pages/s	MB/s
No GW	181	96	547	66	0	13.9	35.8
GW	263	113	778	88	0	11.0	28.6
Squid	299	121	702	104	0	9.9	25.8
Apache 	542	7	20120	1928	42	4.7	7.4
nginx	1246	129	18206	3155	4	2.8	7.2

*GW:Gateway

* - These tests had very high error rates due to network-related issues, so take their numbers with a big grain of salt.
Apache 2 ran fine for several iterations and then started reporting lots of errors like this:
[Thu Aug 29 09:35:17 2013] [error] (49)Can't assign requested address: proxy: HTTP: attempt to connect to 127.0.0.1:9000 (*) failed
I tried changing both to bind on 127.0.0.1 instead of localhost, 
but that didn't help. I'm not sure if they're leaking file descriptors or what's going on.