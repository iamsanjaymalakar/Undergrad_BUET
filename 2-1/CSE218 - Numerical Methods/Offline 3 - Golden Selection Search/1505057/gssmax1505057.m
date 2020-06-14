function [p,max] = gssmax(f,xl,xu,er)

ratio = (5.^0.5-1)./2;

d = ratio.*(xu-xl);

x1 = xl + d;
x2 = xu - d;

f1=f(x1);
f2=f(x2);

it= 0;

while ( abs(xu-xl)>er)
    if(f1 > f2)
        xl = x2;
        x2 = x1;
        x1 = xl + ratio * (xu-xl);
        f2 = f1;
        f1 = f(x1);
    else
        xu = x1;
        x1 = x2;
        x2 = xu - ratio*(xu-xl);
        f1 = f2;
        f2 = f(x2);
    end
    it=it+1;
end

it 

if(f1>f2)
    p = x1;
    max = f1;
else
    p = x2;
    max =f2;
end

end
    
