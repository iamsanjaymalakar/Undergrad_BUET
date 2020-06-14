function [x,iter] = fp_1505057(fun,u,l,error,mi)

fl=fun(l);
fu=fun(u);
prev=0;
error=error/100;
for iter=1:mi
    x = u-( fu*(l-u))/(fl-fu);
    if fun(x)*fun(l)<0 
        u=x;
    else 
        l=x;
    end
    if abs((x-prev)./x)<error
       break;
    else
        prev=x;
    end
end
        
end