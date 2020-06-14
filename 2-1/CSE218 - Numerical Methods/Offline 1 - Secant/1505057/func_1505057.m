function [f]=func_1505057(x)
format long;
k=0.05;
p=3;
f=(x./(1-x).*sqrt(2.*p./(2.+x)))-k;
end