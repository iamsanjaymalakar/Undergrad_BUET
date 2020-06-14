function [root,iter] = secant_1505057(fun,f,s,error,mi)
 
x(1)=f;
x(2)=s;
root=0;
error=error/100;
iter=0;
 
for i=3:mi
   x(i) = x(i-1) - (fun(x(i-1)))*((x(i-1) - x(i-2))/(fun(x(i-1)) - fun(x(i-2))));
    iter=iter+1;
    if abs((x(i)-x(i-1))/x(i))<error
        root=x(i);
        break
    end
end
end