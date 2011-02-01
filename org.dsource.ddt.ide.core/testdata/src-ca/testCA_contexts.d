module testCA_contexts; // TODO: need to test more contexts

import pack.sample;


class /+CC1@+/Foo : /+CC2@+/IFoo {

}


public/+CC3i+/:/+CC3+/  // Probably should test on more DeclarationAttrib

const/+CC4i+/:/+CC4+/

version(blah) /+CC5i+/{:/+CC5+/ // Declaration conditional

/+CC9@+/Foo foo;

}


import pack.mod3;

