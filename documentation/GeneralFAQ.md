
## General FAQ

### What is the history of DDT?

DDT (D Development Tools) is a direct continuation of the [Mmrnmrhm](http://www.dsource.org/projects/descent/wiki/Mmrnmhrm) project, 
started by Bruno Medeiros in 2007. After a hiatus period, development resumed, and so it was renamed to "DDT" in 2010, since a more serious and friendly name was desired.

### Wasn't there already a project named DDT in DSource.org?
Yes. There was a project originally called [EclipseD](http://www.dsource.org/projects/eclipsed), who was later renamed to DDT (around 2005 I think). 
It was long abandoned, and permission was granted by the EclipseD/DDT developer to reclaim the DDT name.


### Can I contribute to DDT? How?
Sure, help is much appreciated. The simple stuff: testing, bug reports, test cases for the test framework is something that anyone can easily contribute. 
If you have more time, grab your Java and Eclipse skills, and contribute some code. See the development wiki entries for more info.
Please direct your questions or comments regarding contributing to the [DDT Discussion Group](http://groups.google.com/group/ddt-ide).

### What do I need to know to open a bug report?
Be prepared to provide basic info about your Eclipse installation, like:
 * The DDT version, can be found in Eclipse under "Help/About/Installation Details/Installed Software", look for DDT entry.
 * The Eclipse log, can be found in Eclipse under "Help/About/Installation Details/Configuration/Error Log", or in the Eclipse workspace directory in the `.metadata/.log` file.


### What's the relation with Descent?
The [Descent](http://www.dsource.org/projects/descent) project was created by Ary Borenszweig (aka Ary Manzana), and had it's first release in late 2006, 
a few months after development of Mmrnmrhm started. Mmrnmrhm was then changed to use some code from Descent, namely the Descent DMD parser, which was part 
of Descent's Java port of the DMD compiler frontend. All the remaining code base was developed separately, and this is still the case today, the only change being 
that the Descent DMD frontend code was separated into it's own plugin: `descent.compiler`.

### Why not develop Descent instead of Mmrnmrhm/DDT?
Mmrnmrhm was first created as part of a MSc. thesis, and because of that a tight control and clear separation of code was necessary.
 That was no longer the case when development of Mmrnmrhm resumed (in early 2010), but nonetheless, even though Descent then had 
 significantly richer functionality than Mmrnmrhm, a technical decision was made to not work with the main Descent code base. 
 (Smaller parts of code might be adapted though, as appropriate).
The full discussion for this can be found on the D.ide newsgroup: 
["Future of Descent and D Eclipse IDE" - D archives](http://www.digitalmars.com/d/archives/digitalmars/D/ide/Future_of_Descent_and_D_Eclipse_IDE_635.html) 
(direct [NNTP link](news://news.digitalmars.com:119/htdofk$2te3$1@digitalmars.com)). In summary the reasoning was:
 * The Descent DMD port required a lot of work to maintain, it was also buggy and did not have good performance (and Ary was not willing to work on it anymore).
 * The bulk of Descent's IDE code (which is code ported from JDT) also had similar problems (although to a lesser scale). 
 A more structured alternative, such as DLTK, was preferred.
