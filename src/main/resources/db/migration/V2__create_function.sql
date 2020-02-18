create or replace function search(ide int, d int)
	returns table(id int, link int , depth int , path int[], cycle bool) as $$
	with recursive search_graph(id, link, depth, path, cycle) as (
		select g.id, g.link , 1, ARRAY[g.id, g.link],
          false
		 from graph g where g.id = ide
		union
		select g.id, g.link, sg.depth + 1,path || g.link,
          g.link = ANY(path)
		from graph g, search_graph sg
		where sg.link = g.id and sg.depth <= d and not cycle
    )
SELECT id, link, depth, path, cycle FROM search_graph where not cycle and array_length(path,1) <= d+1;
$$ language sql;