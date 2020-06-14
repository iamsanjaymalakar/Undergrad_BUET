function res = romberg(f,a,b,er)

X = zeros(10,10);
X(1,1)=trapizoidal(f,1,a,b);
i=0;
for i=1:8
    n=2^i;
    X(i+1,1)=trapizoidal(f,n,a,b);
    for k=2:i+1
        j=2+i-k;
        X(j,k)=(4^(k-1)*X(j+1,k-1)-X(j,k-1))/(4^(k-1)-1);
    end
    err=abs(X(1,i+1)-X(2,i))/X(1,i+1)*100;
    if err<er
        break;
    end
end
i
err
res=X(1,i+1);
end
