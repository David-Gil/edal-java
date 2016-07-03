# ncWMS User Guide

This is the user guide for ncWMS - a piece of software for visualising and exploring environmental data in a browser. It can be run on a server to make your data available over the web or locally for personal use.

To see the capabilities of ncWMS, we have a [demo site available](http://godiva.rdg.ac.uk/ncWMS2/Godiva3.html)

ncWMS is a [Web Map Service](https://en.wikipedia.org/wiki/Web_Map_Service) for geospatial data that are stored in CF-compliant NetCDF files. The intention is to create a WMS that requires minimal configuration: the source data files should already contain most of the necessary metadata. ncWMS is developed and maintained by the Reading e-Science Centre ([ReSC](http://www.met.reading.ac.uk/resc/home/)) at the University of Reading, UK.

ncWMS v2 is build on top of the [EDAL](http://reading-escience-centre.github.io/edal-java/edal_user_guide.html) libraries developed by ReSC

This guide provides instructions on installing, setting up, and using ncWMS v2.

The [source code of this book](https://github.com/Reading-eScience-Centre/edal-java/tree/develop/docs) is hosted on GitHub alongside the source code of ncWMS2. If you have any suggestions for improvement or want to contribute content (to either this guide or ncWMS itself), please create [an issue](https://github.com/Reading-eScience-Centre/edal-java/issues).

![Global sea-surface temperature created by EDAL libraries](images/sst.png)

## Licence
Copyright (c) 2010-2016 The University of Reading
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:
1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. Neither the name of the University of Reading, nor the names of the
   authors or contributors may be used to endorse or promote products
   derived from this software without specific prior written permission.
4. If you wish to use, with or without modification, the Godiva web
   interface, the logo of the Reading e-Science Centre must be retained
   on the web page.
 
THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

## Authors and Contributors

[@guygriffiths](https://github.com/guygriffiths), [@jonblower](https://github.com/jonblower)
