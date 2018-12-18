# Space
functional demo of an issue
I want the object to rotate in the direction it is dragged by the mouse, no matter which 
direction it happens to be orientated at the time. As it is now, when I first drag the
mouse to the right, the object rotates to the right about the screen Y axis as expected;
but then when I drag the mouse upward I want the object to rotate upward about the screen
X axis, but instead it spins to the left about the screen Z axis.

It seems to me that the mouse movement is transforming the objects directly in their
local coordinate system; but instead I think it needs to transform the axis of rotation
itself from the Screen Coordinate System into the Object Coordinate System before applying
it to the object. I don't know, but it may be even more complicated than that.

I would really appreciate any insight or help to resolve this; i'm running out of hair to
pull out... Thanks in advance.
