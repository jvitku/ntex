readme:


pro hloubku listu vetsi nez 5 (do 9ti) je potreba mit novy LaTeX, nebo poupravit enumitem, odsud:

http://stackoverflow.com/questions/1935952/maximum-nesting-level-of-lists-in-latex

""""""""""""""""""""""""
You can use the enumitem package. After what you just have to put the depth level you want:

\usepackage{enumitem}
...
\setlistdepth{9}
And you can have up to 9 nested levels for your lists, easy ;-)

This feature is available in the package since 3.0 (Ubuntu installed me the 2.2 for instance). In case where you have an old version just replace it by: http://ctan.mackichan.com/macros/latex/contrib/enumitem/enumitem.sty

Hope that helps!
"""""""""""""""""""""""




http://ctan.mackichan.com/macros/latex/contrib/enumitem/enumitem.sty