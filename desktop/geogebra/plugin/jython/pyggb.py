# Make division novice-friendly :)
from __future__ import division

# GeoGebra imports
from geogebra.kernel import GeoElement, GeoPoint, GeoNumeric, GeoVector, GeoFunction, GeoText, GeoConic, GeoLine, GeoSegment, GeoRay, GeoBoolean, GeoLocus
from geogebra.kernel import Construction, AlgoDependentNumber, AlgoDependentPoint, AlgoDependentVector
from geogebra.kernel.arithmetic import ExpressionNode, MyVecNode, FunctionVariable, Function as _Function, FunctionNVar, Variable, MyDouble, ExpressionNodeConstants as EC, MyBoolean, NumberValue
from geogebra.euclidian import EuclidianView
from geogebra.plugin.jython import PythonScriptInterface

# Java imports
from javax.swing import JFrame, JPanel, JTextArea, JScrollPane, BoxLayout, JButton, JList, DefaultListModel, ListCellRenderer, BorderFactory, JTextPane
from javax.swing.text import StyleContext, StyleConstants

from java.awt import Component, BorderLayout, Color, GridLayout, Font
from java.awt.event import KeyListener

# Jython imports
import sys

# Python imports
from generic import generic, specmethod, GenericMethods, GenericError, sign


Number = (int, long, float)


class Interface(PythonScriptInterface):
    def init(self, app):
        global _app, _kernel, _cons, _algprocessor
        global selection, pywindow
        _app = app
        _kernel = app.getKernel()
        _cons = _kernel.getConstruction()
        _algprocessor = _kernel.getAlgebraProcessor()
        pywindow = self.pywin = PythonWindow()
        self.geo = Geo()
        selection = self.selection = Selection()
        sys.stdout = self.pywin
        self.namespace = {
            'Color': Color,
            'Point': Point,
            'Element': Element,
            'Numeric': Numeric,
            'Vector': Vector,
            'Line': Line,
            'Segment': Segment,
            'Ray': Ray,
            'Function': Function,
            'Conic': Conic,
            'Circle': Circle,
            'Ellipse': Ellipse,
            'Hyperbola': Hyperbola,
            'Parabola': Parabola,
            'Locus': Locus,
            'Text': Text,
            'Intersect': Intersect,
            'geo': self.geo,
            'selection': self.selection,
            'pointlist': pointlist,
            'interactive': interactive,
        }
        self.namespace.update(unary_functions)
        self.handling_event = False
        self.pywin.add("*** Python Interface initialised ***\n", "output")
    def handleEvent(self, evt_type, target):
        if self.handling_event:
            return
        element = self.geo._get_element(target)
        try:
            action = getattr(element, "on" + evt_type)
        except AttributeError:
            return
        try:
            self.handling_event = True
            action(element)
        except Exception:
            self.pywin.error("Error while handling event '%s' on '%r'"
                           % (evt_type, target))
            raise
        finally:
            self.handling_event = False
    def notifySelected(self, geo, add):
        if self.selection_listener:
            el = self.geo._get_element(geo)
            self.selection_listener.send(el, add)
            if self.selection_listener.done:
                self.remove_selection_listener()
        else:
            # We shouldn't get here :)
            self.remove_selection_listener()
    def toggleWindow(self):
        self.pywin.toggle_visibility()
    def isWindowVisible(self):
        return self.pywin.frame.visible
    def set_selection_listener(self, sl):
        _app.setSelectionListenerMode(_app.getPythonBridge())
        self.selection_listener = sl
    def remove_selection_listener(self):
        _app.setSelectionListenerMode(None)
        self.selection_listener = None
    def compileinteractive(self, source):
        try:
            return compile(source, "<pyggb>", "single")
        except SyntaxError, e:
            if e.msg in (r"no viable alternative at input '<EOF>'",
                         r"mismatched input '\n' expecting INDENT"):
                return "continue"
            else:
                self.pywin.error("Syntax Error: " + e.msg)
                return "error"
    def compilemodule(self, source):
        try:
            return compile(source, "<pyggb>", "exec")
        except SyntaxError, e:
            self.pywin.error("Syntax Error: " + e.msg)
            return "error"
    def run(self, code):
        #stdout = sys.stdout
        #f = Out()
        #sys.stdout = f
        try:
            exec code in self.namespace
        except Exception, e:
            self.pywin.error("Runtime Error: " + str(e))
            raise
        #finally:
        #    sys.stdout = stdout
        #    if f.value.endswith('\n'):
        #        f.value = f.value[:-1]
        #    if f.value:
        #        self.pywin.add(f.value, "output")
        return "OK"

interface = Interface()

class TestSelectionListener(object):
    def __init__(self):
        self.geos = []
        self.done = False
    def __nonzero__(self):
        return not self.done
    def send(self, geo, add):
        self.geos.append(geo)
        if len(self.geos) == 3:
            self.done = True

class interactive(object):
    def __init__(self, f):
        self.objs = []
        self.done = False
        self.f = f
        self.nargs = f.func_code.co_argcount
        interface.set_selection_listener(self)
    def __nonzero__(self):
        return not self.done
    def send(self, obj, add):
        self.objs.append(obj)
        if len(self.objs) == self.nargs:
            self.done = True
            self.f(*self.objs)

LABEL_MODES = ["name", "name+value", "value", "caption"]

class Element(GenericMethods):

    @generic
    def __init__(self, *args, **kwargs):
        elements = [element(arg) for arg in args]
        self.init(*elements)
        for key, value in kwargs.iteritems():
            setattr(self, key, value)

    @generic
    def init(self, *args):
        raise GenericError
    
    @specmethod.__init__
    @sign(GeoElement)
    def initfromgeo(self, geo):
        self.geo = geo
    
    @classmethod
    def fromgeo(cls, geo):
        el = object.__new__(cls)
        el.geo = geo        
        return el

    def __repr__(self):
        return "<%s>" % self.geo
    def __str__(self):
        if self.label is not None:
            return self.label
        else:
            return "(unnamed)"
    
    # property: label 
    def _getlabel(self):
        return self.geo.getLabel()
    def _setlabel(self, label):
        self.geo.setLabel(label)
        self.geo.updateRepaint()
    label = property(_getlabel, _setlabel)

    # property: color
    def _getcolor(self):
        return self.geo.getObjectColor()
    def _setcolor(self, col):
        self.geo.setObjColor(col)
        self.geo.updateRepaint()
    color = property(_getcolor, _setcolor)

    # property: caption
    def _getcaption(self):
        return self.geo.caption
    def _setcaption(self, value):
        self.geo.caption = value
        self.geo.updateRepaint()
    caption = property(_getcaption, _setcaption)

    # property: label_mode
    def _getlabel_mode(self):
        return LABEL_MODES[self.geo.getLabelMode()]
    def _setlabel_mode(self, mode):
        try:
            mode = LABEL_MODES.index(mode)
            self.geo.setLabelMode(mode)
        except ValueError:
            raise ValueError("illegal label mode: %s", mode)
    label_mode = property(_getlabel_mode, _setlabel_mode)

    # property: label_color
    def _getlabel_color(self):
        return self.geo.getLabelColor()
    def _setlabel_color(self, color):
        self.geo.setLabelColor(color)
        self.geo.updateRepaint()
    label_color = property(_getlabel_color, _setlabel_color)
    
    # property: label_visible
    def _getlabel_visible(self):
        return self.geo.isLabelVisible()
    def _setlabel_visible(self, val):
        self.geo.setLabelVisible(bool(val))
    label_visible = property(_getlabel_visible, _setlabel_visible)
    
    # property: background_color
    def _getbgcolor(self):
        return self.geo.backgroundColor
    def _setbgcolor(self, val):
        self.geo.backgroundColor = val
    background_color = property(_getbgcolor, _setbgcolor)
    
    # property: visible
    def _getvisible(self):
        return self.geo.euclidianVisible
    def _setvisible(self, value):
        self.geo.euclidianVisible = bool(value)
        self.geo.updateRepaint()
    visible = property(_getvisible, _setvisible)
    
    def __geo__(self):
        return self.geo


def expr(obj):
    if isinstance(obj, Number):
        return NumberExpression(obj)
    elif isinstance(obj, tuple):
        if len(obj) == 2:
            x, y = obj
            return VectorExpression(x, y)
    elif isinstance(obj, ExpressionNode):
        return Expression.fromnode(obj)
    elif isinstance(obj, Expression):
        return obj
    else:
        raise TypeError("Can't convert object to Expression: %r" % obj)


def element(obj):
    if isinstance(obj, Element):
        return obj
    return Geo._get_element(expr(obj).__geo__())


class Expression(GenericMethods):
    @generic
    def __init__(self, expr):
        self.expr = expr

    @classmethod
    def fromnode(self, node):
        val = node.evaluate()
        if val.isNumberValue():
            return NumberExpression(node)
        elif val.isVectorValue():
            return VectorExpression(node)
        elif val.isBooleanValue():
            return BooleanExpression(node)
        else:
            # TODO add other types of expressions
            return Expression(node)
    
    def __hash__(self):
        return hash(self.geo)
    
    def __expr__(self):
        return self

    def getnode(self):
        if isinstance(self.expr, ExpressionNode):
            return self.expr
        else:
            return ExpressionNode(_kernel, self.expr)
    
    def __repr__(self):
        return "<%s>" % self.expr
    
    def _binop(self, other, opcode, reverse=False):
        try:
            self = expr(self)
            other = expr(other)
        except TypeError:
            return NotImplemented
        if reverse:
            self, other = other, self
        x = self.expr
        y = other.expr
        node = ExpressionNode(_kernel, x, opcode, y)
        return self.fromnode(node)
    
    # Arithmetic operators
    def __add__(self, other):
        return self._binop(other, EC.PLUS)
    def __radd__(self, other):
        return self._binop(other, EC.PLUS, True)
    def __mul__(self, other):
        return self._binop(other, EC.MULTIPLY)
    def __rmul__(self, other):
        return self._binop(other, EC.MULTIPLY, True)
    def __sub__(self, other):
        return self._binop(other, EC.MINUS)
    def __rsub__(self, other):
        return self._binop(other, EC.MINUS, True)
    def __truediv__(self, other):
        return self._binop(other, EC.DIVIDE)
    def __rtruediv__(self, other):
        return self._binop(other, EC.DIVIDE, True)
    def __pow__(self, other):
        return self._binop(other, EC.POWER)
    def __rpow__(self, other):
        return self._binop(other, EC.POWER, True)
    def __neg__(self):
        return self._binop(expr(-1), EC.MULTIPLY, self)
    def __pos__(self):
        return self

    # Comparisons
    def __lt__(self, other):
        return self._binop(other, EC.LESS)
    def __le__(self, other):
        return self._binop(other, EC.LESS_EQUAL)
    def __gt__(self, other):
        return self._binop(other, EC.GREATER)
    def __ge__(self, other):
        return self._binop(other, EC.GREATER_EQUAL)
    def __eq__(self, other):
        return self._binop(other, EC.EQUAL_BOOLEAN)
    def __neq__(self, other):
        return self._binop(other, EC.NOT_EQUAL)

    def _getvalue(self):
        val = self.expr.evaluate()
        if isinstance(val, NumberValue):
            return val.getDouble()
        elif isinstance(val, MyBoolean):
            return val.getBoolean()
        elif isinstance(val, MyVecNode):
            return tuple(val.getCoords())
        else:
            # TODO cover more types
            raise TypeError(type(val))
    value = property(_getvalue)

    def __geo__(self):
        node = self.getnode()
        # Make sure that labels are not created for new geoelements
        flag = _cons.isSuppressLabelsActive()
        try:
            _cons.setSuppressLabelCreation(True)
            [element] = _algprocessor.processExpressionNode(node)
        finally:
            _cons.setSuppressLabelCreation(flag)
        return element


Expressionable = (Number, tuple, Expression)


class NumberExpression(Expression):
    @specmethod.__init__
    @sign(Number)
    def initfromnumber(self, x):
        self.expr = MyDouble(_kernel, float(x))
    
    def __float__(self):
        return self.value

NumberThing = (Number, NumberExpression)


class VectorExpression(Expression):
    @specmethod.__init__
    @sign(NumberThing, NumberThing)
    def initfromcoords(self, x, y):
        x, y = expr(x), expr(y)
        self.expr = MyVecNode(_kernel, x.expr, y.expr)


VectorThing = (tuple, VectorExpression)

class BooleanExpression(Expression):
    # Can this go into Expression?
    def __nonzero__(self):
        return self.value


unary_functions_data = [
    ('cos', EC.COS),
    ('sin', EC.SIN),
    ('tan', EC.TAN),
    ('exp', EC.EXP),
    ('log', EC.LOG),
    ('arccos acos', EC.ARCCOS),
    ('arcsin asin', EC.ARCSIN),
    ('arctan atan', EC.ARCTAN),
    ('sqrt', EC.SQRT),
    ('abs', EC.ABS),
    # TODO Add some more...
]

def unary_factory(name, opcode):
    def f(e):
        return Expression(ExpressionNode(_kernel, expr(e).expr, opcode, None))
    f.__name__ = name
    return f

unary_functions = {}
for names, opcode in unary_functions_data:
    names = names.split()
    f = unary_factory(names[0], opcode)
    for name in names:
        unary_functions[name] = f

binary_functions_data = [
    ('arctan2 atan2', EC.ARCTAN2),
]

# TODO create binary_functions

class ExpressionElement(Element):
    def _getexpr(self):
        return self.geo
    expr = property(_getexpr)


class Numeric(ExpressionElement, NumberExpression):

    @specmethod.__init__
    @sign(Number)
    def initfromnumber(self, val):
        self.geo = GeoNumeric(_cons, float(val))
    
    @specmethod.__init__
    @sign(Expression)
    def initfromexpr(self, e):
        algo = AlgoDependentNumber(_cons, e.expr, False)
        self.geo = algo.number
    
    def __float__(self):
        return self.geo.value
    

class Boolean(ExpressionElement, Expression):
    pass


class VectorOrPoint(ExpressionElement, Expression):

    @specmethod.__init__
    @sign(NumberThing, NumberThing)
    def initfromexprcoords(self, xe, ye):
        ve = VectorExpression(xe, ye)
        self.initfromexpr(ve)
        
    def setcoords(self, x, y):
        self.geo.setCoords(float(x), float(y), 1.0)
        self.geo.updateRepaint()
    def _getcoords(self):
        return (self.x, self.y)
    def _setcoords(self, coords):
        self.setcoords(*coords)
    coords = property(_getcoords, _setcoords)
    
    def _getx(self):
        try:
            return self._x
        except AttributeError:
            xe = ExpressionNode(_kernel, self.geo, EC.XCOORD, None)
            self._x = NumberExpression(xe)
            return self._x
    def _setx(self, x):
            self.setcoords(x, self.y.value)
    x = property(_getx, _setx)

    def _gety(self):
        try:
            return self._y
        except AttributeError:
            ye = ExpressionNode(_kernel, self.geo, EC.YCOORD, None)
            self._y = NumberExpression(ye)
            return self._y
    def _sety(self, y):
            self.setcoords(self.x.value, y)
    y = property(_gety, _sety)


class Vector(VectorOrPoint):
    @specmethod.__init__
    @sign(VectorThing)
    def initfromexpr(self, e):
        algo = AlgoDependentVector(_cons, expr(e).getnode())
        self.geo = algo.vector

    @specmethod.__init__
    @sign(Number, Number)
    def initfromnumbercoords(self, x, y):
        self.geo = _kernel.Vector(None, float(x), float(y))

        
class Point(VectorOrPoint):
    @specmethod.__init__
    @sign(VectorThing)
    def initfromexpr(self, e):
        algo = AlgoDependentPoint(_cons, expr(e).getnode(), False)
        self.geo = algo.point

    @specmethod.__init__
    @sign(Number, Number)
    def initfromnumbercoords(self, x, y):
        self.geo = _kernel.Point(None, float(x), float(y))


@Vector.init.spec
@sign(Vector, Point, Point)
def initfrompoints(self, p, q):
    self.geo = _kernel.Vector(None, p.geo, q.geo)

@Vector.init.spec
@sign(Vector, Point)
def initfrompoint(self, p):
    self.geo = _kernel.Vector(None, p.geo)

@Point.init.spec
@sign(Point, Point)
def initfrompoint(self, p):
    self.geo = p.geo


class Path(Element):

    _line_types = {
        'full': EuclidianView.LINE_TYPE_FULL,
        'short-dash': EuclidianView.LINE_TYPE_DASHED_SHORT,
        'long-dash': EuclidianView.LINE_TYPE_DASHED_LONG,
        'dot': EuclidianView.LINE_TYPE_DASHED_DOTTED,
        'dash-dot': EuclidianView.LINE_TYPE_DOTTED,
    }
    _rev_line_types = dict((v, k) for k, v in _line_types.iteritems())
    
    def _get_thickness(self):
        return self.geo.lineThickness
    def _set_thickness(self, val):
        self.geo.lineThickness = val
        self.geo.updateRepaint()
    thickness = property(_get_thickness, _set_thickness)

    def _get_linetype(self):
        return self._rev_line_types[self.geo.lineType]
    def _set_linetype(self, val):
        try:
            self.geo.lineType = self._line_types[val]
        except KeyError:
            raise ValueError("Unknown line type: %r" % val)
        self.geo.updateRepaint()
    linetype = property(_get_linetype, _set_linetype)


class Line(Path):

    @specmethod.init
    @sign(Point, Point)
    def initfrom2points(self, p, q):
        self.geo = _kernel.Line(None, p.geo, q.geo)

    @specmethod.init
    @sign(Point, Vector)
    def initfrompointandvector(self, p, v):
        self.geo = _kernel.Line(None, p.geo, v.geo)

    
    def __contains__(self, p):
        if isinstance(p, Point):
            return self.geo.isOnPath(p.geo, 1e-5)
        else:
            return False
    
    def _getdirection(self):
        return Vector.fromgeo(_kernel.Direction(self.geo))
    direction = property(_getdirection)

@Line.init.spec
@sign(Line, Point, Line)
def initfrompointandline(self, p, l):
    return _kernel.Line(None, p.geo, l.geo)


class Segment(Line, ExpressionElement, NumberExpression):
    @specmethod.init
    @sign(Point, Point)
    def initfrompoints(self, p, q):
        self.geo = _kernel.Segment(None, p.geo, q.geo)

    # property: startpoint    
    def _getstartpoint(self):
        return Geo._get_element(self.geo.getStartPoint())
    def _setstartpoint(self, p):
        self.geo.setStartPoint(element(p).geo)
    startpoint = property(_getstartpoint, _setstartpoint)
    
    # property: endpoint
    def _getendpoint(self):
        return Geo._get_element(self.geo.getEndPoint())
    def _setendpoint(self):
        self.geo.setEndPoint(element(p).geo)
    endpoint = property(_getendpoint, _setendpoint)


class Ray(Line):
    @specmethod.init
    @sign(Point, Point)
    def initfrompoints(self, p, q):
        self.geo = _kernel.Ray(None, p.geo, q.geo)


class Text(Element):
    def __init__(self, value, location=None):
        self.geo = _kernel.Text(None, value)
        if isinstance(location, Point):
            self.geo.startPoint = location.geo
        elif location is not None:
            raise TypeError
        self.geo.updateRepaint()

    def _setorigin(self, point):
        if isinstance(point, Point):
            self.geo.startPoint = point.geo
        else:
            raise TypeError
        self.geo.updateRepaint()
    def _getorigin(self):
        return Point.fromgeo(self.geo.startPoint)
    def _delorigin(self):
        self.geo.removeStartPoint(self.geo.startPoint)
        self.geo.updateRepaint()
    origin = property(_getorigin, _setorigin, _delorigin)


class Conic(Path):

    @classmethod
    def fromgeo(cls, geo):
        if geo.keepsType():
            conictype = cls._conic_types.get(geo.getTypeString(), cls)
        else:
            conictype = cls
        el = object.__new__(conictype)
        el.geo = geo
        return el
    
    @specmethod.init
    @sign(Point, Point, Point, Point, Point)
    def initfrom5points(self, *points):
        geos = [p.geo for p in points]
        self.geo = _kernel.Conic(None, geos)

    @specmethod.init
    @sign(Numeric, Numeric, Numeric, Numeric, Numeric, Numeric)
    def initfrom6coeffs(self, *coeffs):
        geos = [c.geo for c in coeffs]
        self.geo = _kernel.Conic(None, geos)


class Circle(Conic):
    
    @specmethod.init
    @sign(Point, Point)
    def initfromcentreandpoint(self, p, q):
        self.geo = _kernel.Circle(None, p.geo, q.geo)

    @specmethod.init
    @sign(Point, Point, Point)
    def initfrom3points(self, p, q, r):
        self.geo = _kernel.Circle(None, p.geo, q.geo, r.geo)

    @specmethod.init
    @sign(Point, Segment)
    def initfromcentreandsegment(self, c, s):
        self.geo = _kernel.Circle(None, c.geo, s.geo)

    @specmethod.init
    @sign(Point, Numeric)
    def initfromcentreandradius(self, c, r):
        self.geo = _kernel.Circle(None, c.geo, element(r).geo)


class Ellipse(Conic):
    
    @specmethod.init
    @sign(Point, Point, Numeric)
    def initfromfociandhalfmajoraxis(self, p, q, a):
        self.geo = _kernel.Ellipse(None, p.geo, q.geo, a.geo)

    @specmethod.init
    @sign(Point, Point, Point)
    def initfromfociandpoint(self, p, q, r):
        self._geo = _kernel.Ellipse(None, p.geo, q.geo, r.geo)


class Hyperbola(Conic):
    
    @specmethod.init
    @sign(Point, Point, Numeric)
    def initfromfociandhalfmajoraxis(self, p, q, a):
        self.geo = _kernel.Hyperbola(None, p.geo, q.geo, a.geo)

    @specmethod.init
    @sign(Point, Point, Point)
    def initfromfociandpoint(self, p, q, r):
        self._geo = _kernel.Hyperbola(None, p.geo, q.geo, r.geo)


class Parabola(Conic):
    
    @specmethod.init
    @sign(Point, Line)
    def initfromfocusanddirectrix(self, p, l):
        self._geo = _kernel.Parabola(None, p.geo, l.geo)


Conic._conic_types = {
    'Circle': Circle,
    'Parabola': Parabola,
    'Hyperbola': Hyperbola,
    'Ellipse': Ellipse,
}
    

class Locus(Path):
    
    def get_point(self, param):
        if param < 0 or param > 1:
            raise ValueError("param must be between 0 and 1")
        return _kernel.Point(None, self.geo, element(param).geo)


class ImplicitPoly(Path):

    pass


class Function(Element):
    
    def __init__(self, f):
        self.nargs = nargs = f.func_code.co_argcount
        self.varnames = varnames = f.func_code.co_varnames
        if nargs == 0:
            raise ValueError("function must have at least one variable")
        if nargs == 1:
            x = FunctionVariable(_kernel, varnames[0])
            fx = f(Expression(x))
            func = _Function(fx.expr, x)
            [self.geo] = _algprocessor.processFunction(None, func)
        else:
            xs = [FunctionVariable(_kernel, v) for v in varnames[:nargs]]
            fxs = f(*map(Expression, xs))
            func = FunctionNVar(fxs.expr, xs)
            #func.initFunction()
            #self.geo = _kernel.FunctionNVar(f.__name__, func)
            [self.geo] = _algprocessor.processFunctionNVar(None, func)
    
    def __call__(self, *args):
        args = map(expr, args)

    def _getimplicitcurve(self):
        if self.nargs != 2:
            raise AttributeError
        else:
            geo = _kernel.ImplicitPoly(None, self.geo)
            return ImplicitPoly(geo)
    implicitcurve = property(_getimplicitcurve)

class Intersect(GenericMethods):
    
    def __init__(self, *args):
        elements = [element(x) for x in args]
        geos = self.find(*elements)
        self.intersections = map(Geo._get_element, geos)

    @generic
    def find(self, *args):
        raise GenericError

    @specmethod.find
    @sign(Line, Line)
    def twolines(self, l1, l2):
        return [_kernel.IntersectLines(None, l1.geo, l2.geo)]

    @specmethod.find
    @sign(Line, Conic)
    def lineconic(self, l, c):
        return _kernel.IntersectLineConic(None, l.geo, c.geo)

    @specmethod.find
    @sign(Conic, Line)
    def conicline(self, c, l):
        return self.lineconic(l, c)

    @specmethod.find
    @sign(Conic, Conic)
    def twoconics(self, c1, c2):
        return _kernel.IntersectConics(None, c1.geo, c2.geo)
    
    def __repr__(self):
        return "[%s]" % ", ".join(map(repr, self.intersections))

    def __getitem__(self, i):
        return self.intersections[i]

    def __iter__(self):
        for i in self.intersections:
            yield i


class Selection(object):
    @property
    def all(self):
        return map(Geo._get_element, _app.getSelectedGeos())
    
    def filter(self, eltype):
        return [x for x in self.all if isinstance(x, eltype)]
    
    @property
    def points(self):
        return self.filter(Point)

    @property
    def lines(self):
        return self.filter(Line)
    
    @property
    def segments(self):
        return self.filter(Segment)
    
    @property
    def vectors(self):
        return self.filter(Vector)


def pointlist():
    classtype = GeoElement.GEO_CLASS_POINT
    pointset = _cons.getGeoSetLabelOrder(classtype)
    pointlist = []
    it = pointset.iterator()
    while it.hasNext():
        pointlist.append(Point.fromgeo(it.next()))
    return pointlist

class Geo(object):
    _map = {
        GeoVector: Vector,
        GeoPoint: Point,
        GeoNumeric: Numeric,
        GeoBoolean: Boolean,
        GeoFunction: Function,
        GeoText: Text,
        GeoLine: Line,
        GeoSegment: Segment,
        GeoRay: Ray,
        GeoConic: Conic,
        GeoLocus: Locus,
    }
    _revmap = dict((v, k) for k, v in _map.iteritems())
    _cache = {}
    @classmethod
    def _get_element(cls, geo):
        if geo is None:
            return None
        elif geo in cls._cache:
            return cls._cache[geo]
        else:
            eltype = cls._map.get(type(geo))
            if eltype is None:
                return None
            element = eltype.fromgeo(geo)
            cls._cache[geo] = element
            return element
    def __getattr__(self, name):
        geo = _kernel.lookupLabel(name)
        return self._get_element(geo)
    def __setattr__(self, name, value):
        if not isinstance(value, Element):
            node = expr(value).getnode()
            node.label = name
            value = element(Expression(node))
        value.label = name


class MyListCellRenderer(ListCellRenderer):
    colormap = {
        "input": Color.BLACK,
        "output": Color.BLUE,
        "error": Color.RED
    }
    font = Font("Monospaced", Font.PLAIN, 12)
    def getListCellRendererComponent(self, lst, value, index, isSelected, cellHasFocus):
        text = value["text"]
        renderer = JTextArea(text=text)
        renderer.foreground = self.colormap[value["type"]]
        renderer.font = self.font
        if isSelected:
            renderer.background = Color.YELLOW
        return renderer


class OutputPane(object):
    def __init__(self):
        self.textpane = JTextPane()
        self.doc = self.textpane.getStyledDocument()
        self.textpane.editable = False
        default_style = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE)
        parent_style = self.doc.addStyle("parent", default_style)
        StyleConstants.setFontFamily(parent_style, "Monospaced")
        input_style = self.doc.addStyle("input", parent_style)
        output_style = self.doc.addStyle("output", parent_style)
        StyleConstants.setForeground(output_style, Color.BLUE)
        error_style = self.doc.addStyle("error", parent_style)
        StyleConstants.setForeground(error_style, Color.RED)
    def addtext(self, text, style="input", ensure_newline=False):
        doclen = self.doc.length
        if ensure_newline and self.doc.getText(doclen - 1, 1) != '\n':
            text = '\n' + text
        self.doc.insertString(self.doc.length, text, self.doc.getStyle(style))
        # Scroll down
        self.textpane.setCaretPosition(self.doc.length)


class PythonWindow(KeyListener):
    def __init__(self):
        self.frame = JFrame("Python Window")
        #self.historyList = JList(DefaultListModel())
        #self.historyList.cellRenderer = MyListCellRenderer()
        scrollpane = JScrollPane()
        inputPanel = JPanel()
        inputPanel.layout = GridLayout(1, 1)
        self.input = JTextArea("")
        self.input.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        self.input.tabSize = 4
        self.input.font = Font("Monospaced", Font.PLAIN, 12)
        self.input.addKeyListener(self)
        inputPanel.add(self.input)
        self.outputpane = OutputPane()
        scrollpane.viewport.view = self.outputpane.textpane #self.historyList
        self.frame.add(scrollpane, BorderLayout.CENTER)
        self.frame.add(inputPanel, BorderLayout.PAGE_END)
        self.frame.size = 500, 600
        self.frame.visible = False
        self.component = None
    def toggle_visibility(self):
        self.frame.visible = not self.frame.visible
    def add_component(self, c):
        self.remove_component()
        self.frame.add(c, BorderLayout.PAGE_START)
        self.component = c
    def remove_component(self):
        if self.component is not None:
            self.frame.remove(self.component)
            self.component = None
    def add(self, text, type="input"):
        self.outputpane.addtext(text, type)
        #self.historyList.model.addElement({"text": text, "type": type})
        #self.historyList.validate()
        self.frame.validate()
        #last = self.historyList.model.getSize() - 1
        #self.historyList.ensureIndexIsVisible(last)
    def error(self, text):
        self.outputpane.addtext(text, "error", ensure_newline=True)
    def write(self, text):
        self.add(text, "output")
    def run(self, evt):
        source = self.input.text
        if not source.strip():
            self.input.text = ""
            return
        processed_source = source.replace("$", "geo.")
        code = interface.compileinteractive(processed_source)
        if code in ("continue", "error"):
            code = interface.compilemodule(processed_source)
            if code == "error":
                return
        source = source.strip() + '\n'
        self.outputpane.addtext(source, "input", ensure_newline=True)
        result = interface.run(code)
        if result == "OK":
            self.input.text = ""
    def keyPressed(self, evt):
        pass
    def keyReleased(self, evt):
        pass
    def keyTyped(self, evt):
        if evt.keyChar == '\n':
            # Only try to run compound statements when they end with
            # two \n
            source = self.input.text
            lines = source.split("\n")
            if lines[0].rstrip().endswith(":") and not source.endswith("\n\n"):
                for i, c in enumerate(lines[-2]):
                    if c not in ' \t': break
                else:
                    self.run(evt)
                    return
                prefix = lines[-2][:i]
                if lines[-2].endswith(":"):
                    prefix += '\t'
                self.input.text = source + prefix
            else:
                self.run(evt)

