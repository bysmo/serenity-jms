'use client';

import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Checkbox } from '@/components/ui/checkbox';
import {
  ChevronLeft,
  ChevronRight,
  ChevronsLeft,
  ChevronsRight,
  MoreHorizontal,
  Search,
  ArrowUpDown,
  ArrowUp,
  ArrowDown,
  Download,
} from 'lucide-react';
import { useState, useMemo, type ReactNode } from 'react';

// ─── Column Definition ────────────────────────────────────────

export interface Column<T> {
  key: string;
  header: string;
  render?: (item: T) => ReactNode;
  sortable?: boolean;
  width?: string;
}

// ─── Sort State ───────────────────────────────────────────────

export interface SortState {
  key: string;
  direction: 'asc' | 'desc';
}

// ─── Pagination State ─────────────────────────────────────────

export interface PaginationState {
  pageIndex: number;   // 0-based
  pageSize: number;
}

// ─── Props ────────────────────────────────────────────────────

interface DataTableProps<T extends Record<string, unknown>> {
  data: T[];
  columns: Column<T>[];
  keyExtractor: (item: T) => string;

  // Search
  searchable?: boolean;
  searchPlaceholder?: string;
  searchKeys?: (keyof T)[];

  // Filters
  filters?: {
    key: string;
    label: string;
    options: { label: string; value: string }[];
  }[];

  // Row actions
  actions?: (item: T) => {
    label: string;
    onClick: () => void;
    variant?: 'default' | 'destructive';
    icon?: ReactNode;
    separator?: boolean;
  }[];

  // Pagination
  pageSize?: number;
  pageSizeOptions?: number[];
  serverSidePagination?: boolean;
  totalItems?: number;

  // Selection
  selectable?: boolean;
  onSelectionChange?: (selectedItems: T[]) => void;

  // Sorting
  onSort?: (sort: SortState | null) => void;

  // Export
  exportable?: boolean;
  exportFilename?: string;

  // Loading
  loading?: boolean;

  // Callback when pagination changes (for server-side)
  onPageChange?: (pagination: PaginationState) => void;
}

// ─── Component ────────────────────────────────────────────────

export function DataTable<T extends Record<string, unknown>>({
  data,
  columns,
  keyExtractor,
  searchable = true,
  searchPlaceholder = 'Rechercher...',
  searchKeys,
  filters = [],
  actions,
  pageSize: initialPageSize = 10,
  pageSizeOptions = [5, 10, 25, 50, 100],
  serverSidePagination = false,
  totalItems: serverTotalItems,
  selectable = false,
  onSelectionChange,
  onSort,
  exportable = false,
  exportFilename = 'export',
  loading = false,
  onPageChange,
}: DataTableProps<T>) {
  // ── State ──
  const [searchQuery, setSearchQuery] = useState('');
  const [activeFilters, setActiveFilters] = useState<Record<string, string>>({});
  const [pagination, setPagination] = useState<PaginationState>({
    pageIndex: 0,
    pageSize: initialPageSize,
  });
  const [sort, setSort] = useState<SortState | null>(null);
  const [selectedKeys, setSelectedKeys] = useState<Set<string>>(new Set());

  // ── Filter data ──
  const filteredData = useMemo(() => {
    let result = [...data];

    // Search
    if (searchQuery) {
      const q = searchQuery.toLowerCase();
      result = result.filter((item) => {
        if (searchKeys && searchKeys.length > 0) {
          return searchKeys.some((key) => {
            const val = item[key];
            return val != null && String(val).toLowerCase().includes(q);
          });
        }
        return Object.values(item).some(
          (val) => val != null && String(val).toLowerCase().includes(q)
        );
      });
    }

    // Filters
    Object.entries(activeFilters).forEach(([key, value]) => {
      if (value && value !== '__all__') {
        result = result.filter((item) => String(item[key]) === value);
      }
    });

    return result;
  }, [data, searchQuery, searchKeys, activeFilters]);

  // ── Sort data ──
  const sortedData = useMemo(() => {
    if (!sort || serverSidePagination) return filteredData;
    const { key, direction } = sort;
    return [...filteredData].sort((a, b) => {
      const aVal = a[key];
      const bVal = b[key];
      if (aVal == null && bVal == null) return 0;
      if (aVal == null) return 1;
      if (bVal == null) return -1;
      let cmp = 0;
      if (typeof aVal === 'number' && typeof bVal === 'number') {
        cmp = aVal - bVal;
      } else {
        cmp = String(aVal).localeCompare(String(bVal), 'fr', { numeric: true });
      }
      return direction === 'asc' ? cmp : -cmp;
    });
  }, [filteredData, sort, serverSidePagination]);

  // ── Pagination ──
  const totalItems = serverSidePagination ? (serverTotalItems ?? 0) : sortedData.length;
  const totalPages = Math.max(1, Math.ceil(totalItems / pagination.pageSize));
  const safePageIndex = Math.min(pagination.pageIndex, totalPages - 1);

  const paginatedData = useMemo(() => {
    if (serverSidePagination) return sortedData;
    const start = safePageIndex * pagination.pageSize;
    return sortedData.slice(start, start + pagination.pageSize);
  }, [sortedData, safePageIndex, pagination.pageSize, serverSidePagination]);

  // ── Selection ──
  const allPageSelected =
    paginatedData.length > 0 &&
    paginatedData.every((item) => selectedKeys.has(keyExtractor(item)));
  const somePageSelected =
    paginatedData.some((item) => selectedKeys.has(keyExtractor(item))) && !allPageSelected;

  const handleSelectAll = () => {
    const newSet = new Set(selectedKeys);
    if (allPageSelected) {
      paginatedData.forEach((item) => newSet.delete(keyExtractor(item)));
    } else {
      paginatedData.forEach((item) => newSet.add(keyExtractor(item)));
    }
    setSelectedKeys(newSet);
    onSelectionChange?.(data.filter((item) => newSet.has(keyExtractor(item))));
  };

  const handleSelectRow = (item: T) => {
    const newSet = new Set(selectedKeys);
    const key = keyExtractor(item);
    if (newSet.has(key)) {
      newSet.delete(key);
    } else {
      newSet.add(key);
    }
    setSelectedKeys(newSet);
    onSelectionChange?.(data.filter((i) => newSet.has(keyExtractor(i))));
  };

  // ── Page change handler ──
  const updatePagination = (newPagination: PaginationState) => {
    setPagination(newPagination);
    onPageChange?.(newPagination);
  };

  const goToPage = (pageIndex: number) => {
    updatePagination({ ...pagination, pageIndex: Math.max(0, Math.min(pageIndex, totalPages - 1)) });
  };

  // ── Sort handler ──
  const handleSort = (key: string) => {
    let newSort: SortState | null;
    if (sort?.key === key) {
      if (sort.direction === 'asc') {
        newSort = { key, direction: 'desc' };
      } else {
        newSort = null;
      }
    } else {
      newSort = { key, direction: 'asc' };
    }
    setSort(newSort);
    onSort?.(newSort);
    goToPage(0);
  };

  // ── Export CSV ──
  const handleExport = () => {
    const exportData = sortedData;
    const headers = columns.map((c) => c.header).join(',');
    const rows = exportData.map((item) =>
      columns
        .map((c) => {
          const val = item[c.key];
          const str = val != null ? String(val) : '';
          return str.includes(',') || str.includes('"') ? `"${str.replace(/"/g, '""')}"` : str;
        })
        .join(',')
    );
    const csv = [headers, ...rows].join('\n');
    const blob = new Blob(['\ufeff' + csv], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = `${exportFilename}.csv`;
    link.click();
    URL.revokeObjectURL(link.href);
  };

  // ── Page numbers to display ──
  const getPageNumbers = () => {
    const pages: (number | 'ellipsis')[] = [];
    const current = safePageIndex + 1;
    const total = totalPages;

    if (total <= 7) {
      for (let i = 1; i <= total; i++) pages.push(i);
    } else {
      pages.push(1);
      if (current > 3) pages.push('ellipsis');
      for (
        let i = Math.max(2, current - 1);
        i <= Math.min(total - 1, current + 1);
        i++
      ) {
        pages.push(i);
      }
      if (current < total - 2) pages.push('ellipsis');
      pages.push(total);
    }
    return pages;
  };

  // ── Items range display ──
  const startItem = safePageIndex * pagination.pageSize + 1;
  const endItem = Math.min((safePageIndex + 1) * pagination.pageSize, totalItems);

  return (
    <div className="space-y-4">
      {/* ── Toolbar: Search, Filters, Export ── */}
      {(searchable || filters.length > 0 || exportable) && (
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
            {searchable && (
              <div className="relative flex-1 max-w-sm">
                <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                <Input
                  placeholder={searchPlaceholder}
                  value={searchQuery}
                  onChange={(e) => {
                    setSearchQuery(e.target.value);
                    goToPage(0);
                  }}
                  className="pl-9"
                />
              </div>
            )}
            <div className="flex flex-wrap gap-2">
              {filters.map((filter) => (
                <Select
                  key={filter.key}
                  value={activeFilters[filter.key] || '__all__'}
                  onValueChange={(value) => {
                    setActiveFilters((prev) => ({ ...prev, [filter.key]: value }));
                    goToPage(0);
                  }}
                >
                  <SelectTrigger className="w-[170px]">
                    <SelectValue placeholder={filter.label} />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="__all__">Tous</SelectItem>
                    {filter.options.map((opt) => (
                      <SelectItem key={opt.value} value={opt.value}>
                        {opt.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              ))}
            </div>
          </div>
          {exportable && (
            <Button variant="outline" size="sm" onClick={handleExport} className="gap-2">
              <Download className="h-4 w-4" />
              Exporter CSV
            </Button>
          )}
        </div>
      )}

      {/* ── Selection info bar ── */}
      {selectable && selectedKeys.size > 0 && (
        <div className="flex items-center gap-2 rounded-md bg-muted/50 px-4 py-2 text-sm">
          <span className="font-medium">{selectedKeys.size}</span>
          <span className="text-muted-foreground">élément(s) sélectionné(s)</span>
          <Button
            variant="ghost"
            size="sm"
            className="ml-auto h-7 text-xs"
            onClick={() => {
              setSelectedKeys(new Set());
              onSelectionChange?.([]);
            }}
          >
            Tout désélectionner
          </Button>
        </div>
      )}

      {/* ── Table ── */}
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              {selectable && (
                <TableHead className="w-[40px] pl-4">
                  <Checkbox
                    checked={allPageSelected ? true : somePageSelected ? 'indeterminate' : false}
                    onCheckedChange={handleSelectAll}
                    aria-label="Sélectionner tout"
                  />
                </TableHead>
              )}
              {columns.map((col) => (
                <TableHead
                  key={col.key}
                  style={col.width ? { width: col.width } : undefined}
                  className={col.sortable ? 'cursor-pointer select-none hover:bg-muted/50' : ''}
                  onClick={col.sortable ? () => handleSort(col.key) : undefined}
                >
                  <div className="flex items-center gap-1">
                    {col.header}
                    {col.sortable && sort?.key === col.key && (
                      sort.direction === 'asc' ? (
                        <ArrowUp className="h-3.5 w-3.5 text-primary" />
                      ) : (
                        <ArrowDown className="h-3.5 w-3.5 text-primary" />
                      )
                    )}
                    {col.sortable && sort?.key !== col.key && (
                      <ArrowUpDown className="h-3.5 w-3.5 text-muted-foreground/50" />
                    )}
                  </div>
                </TableHead>
              ))}
              {actions && <TableHead className="w-[50px]">Actions</TableHead>}
            </TableRow>
          </TableHeader>
          <TableBody>
            {loading ? (
              <TableRow>
                <TableCell
                  colSpan={columns.length + (actions ? 1 : 0) + (selectable ? 1 : 0)}
                  className="h-24 text-center text-muted-foreground"
                >
                  <div className="flex items-center justify-center gap-2">
                    <div className="h-4 w-4 animate-spin rounded-full border-2 border-primary border-t-transparent" />
                    Chargement...
                  </div>
                </TableCell>
              </TableRow>
            ) : paginatedData.length === 0 ? (
              <TableRow>
                <TableCell
                  colSpan={columns.length + (actions ? 1 : 0) + (selectable ? 1 : 0)}
                  className="h-24 text-center text-muted-foreground"
                >
                  <div className="flex flex-col items-center gap-1">
                    <Search className="h-8 w-8 text-muted-foreground/40" />
                    <p className="text-sm font-medium">Aucune donnée trouvée</p>
                    <p className="text-xs text-muted-foreground">
                      Essayez de modifier vos critères de recherche ou vos filtres
                    </p>
                  </div>
                </TableCell>
              </TableRow>
            ) : (
              paginatedData.map((item) => {
                const key = keyExtractor(item);
                const isSelected = selectedKeys.has(key);
                return (
                  <TableRow
                    key={key}
                    data-state={isSelected ? 'selected' : undefined}
                    className={isSelected ? 'bg-primary/5' : ''}
                  >
                    {selectable && (
                      <TableCell className="pl-4">
                        <Checkbox
                          checked={isSelected}
                          onCheckedChange={() => handleSelectRow(item)}
                          aria-label={`Sélectionner ${key}`}
                        />
                      </TableCell>
                    )}
                    {columns.map((col) => (
                      <TableCell key={col.key}>
                        {col.render ? col.render(item) : String(item[col.key] ?? '')}
                      </TableCell>
                    ))}
                    {actions && (
                      <TableCell>
                        <DropdownMenu>
                          <DropdownMenuTrigger asChild>
                            <Button variant="ghost" size="icon" className="h-8 w-8">
                              <MoreHorizontal className="h-4 w-4" />
                            </Button>
                          </DropdownMenuTrigger>
                          <DropdownMenuContent align="end" className="w-48">
                            {actions(item).map((action, idx, arr) => (
                              <span key={idx}>
                                <DropdownMenuItem
                                  onClick={action.onClick}
                                  className={
                                    action.variant === 'destructive'
                                      ? 'text-destructive focus:text-destructive'
                                      : ''
                                  }
                                >
                                  {action.icon && (
                                    <span className="mr-2 inline-flex">{action.icon}</span>
                                  )}
                                  {action.label}
                                </DropdownMenuItem>
                                {(action.separator || (arr[idx + 1]?.variant === 'destructive' && action.variant !== 'destructive')) && (
                                  <DropdownMenuSeparator />
                                )}
                              </span>
                            ))}
                          </DropdownMenuContent>
                        </DropdownMenu>
                      </TableCell>
                    )}
                  </TableRow>
                );
              })
            )}
          </TableBody>
        </Table>
      </div>

      {/* ── Pagination Footer ── */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        {/* Left: Items count + page size selector */}
        <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:gap-4">
          <p className="text-sm text-muted-foreground">
            {totalItems === 0 ? (
              'Aucun résultat'
            ) : (
              <>
                Affichage de{' '}
                <span className="font-medium text-foreground">{startItem}</span>
                {' à '}
                <span className="font-medium text-foreground">{endItem}</span>
                {' sur '}
                <span className="font-medium text-foreground">{totalItems}</span>
                {' résultat(s)'}
              </>
            )}
          </p>
          <div className="flex items-center gap-2">
            <span className="text-sm text-muted-foreground">Afficher</span>
            <Select
              value={String(pagination.pageSize)}
              onValueChange={(value) => {
                const newSize = Number(value);
                updatePagination({ pageIndex: 0, pageSize: newSize });
              }}
            >
              <SelectTrigger className="h-8 w-[70px]">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {pageSizeOptions.map((size) => (
                  <SelectItem key={size} value={String(size)}>
                    {size}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            <span className="text-sm text-muted-foreground">par page</span>
          </div>
        </div>

        {/* Right: Page navigation */}
        {totalPages > 1 && (
          <div className="flex items-center gap-1">
            <Button
              variant="outline"
              size="icon"
              className="h-8 w-8"
              onClick={() => goToPage(0)}
              disabled={safePageIndex === 0}
              title="Première page"
            >
              <ChevronsLeft className="h-4 w-4" />
            </Button>
            <Button
              variant="outline"
              size="icon"
              className="h-8 w-8"
              onClick={() => goToPage(safePageIndex - 1)}
              disabled={safePageIndex === 0}
              title="Page précédente"
            >
              <ChevronLeft className="h-4 w-4" />
            </Button>

            {getPageNumbers().map((page, idx) =>
              page === 'ellipsis' ? (
                <span key={`ellipsis-${idx}`} className="px-1 text-muted-foreground">
                  ...
                </span>
              ) : (
                <Button
                  key={page}
                  variant={page === safePageIndex + 1 ? 'default' : 'outline'}
                  size="icon"
                  className="h-8 w-8 text-xs"
                  onClick={() => goToPage(page - 1)}
                >
                  {page}
                </Button>
              )
            )}

            <Button
              variant="outline"
              size="icon"
              className="h-8 w-8"
              onClick={() => goToPage(safePageIndex + 1)}
              disabled={safePageIndex >= totalPages - 1}
              title="Page suivante"
            >
              <ChevronRight className="h-4 w-4" />
            </Button>
            <Button
              variant="outline"
              size="icon"
              className="h-8 w-8"
              onClick={() => goToPage(totalPages - 1)}
              disabled={safePageIndex >= totalPages - 1}
              title="Dernière page"
            >
              <ChevronsRight className="h-4 w-4" />
            </Button>
          </div>
        )}
      </div>
    </div>
  );
}
