function r = getEMIS(chainlist)
r = zeros(11*13,2);

num_of_list = size(chainlist,2);


for i = 1 : num_of_list
    chain = chainlist{i};
    num_of_state = size(chain,2);
    
    for j = 1: num_of_state
       state = chain(j);
       r(state,1) = 1;
    end
end


end