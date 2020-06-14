function y=s1505057_myCos(x,n)
format long;
ans=1;
for i=1:n-1
    ans=ans+(-1).^i.*(x.^(2.*i))./factorial(2.*i);
end
if n==1
    y=1;
else
    y=ans;
end
end