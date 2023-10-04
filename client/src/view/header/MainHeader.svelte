<script>
    import { createEventDispatcher, onMount } from "svelte";
    import Toolbar from "../../widget/toolbar/Toolbar.svelte";
    import Button from "../../widget/button/Button.svelte";
    import TextField from "../../widget/fields/TextField.svelte";
    import MobileSearch from "./MobileSearch.svelte";
    import Utils from "../../util/Utils";
    import Boot from "../../util/Boot";
    import Labels from "../../const/Labels";
    import Request from "../../util/Request";
    import SessionUtil from "../../util/SessionUtil";
    import urlConst from "../../const/Url";

    import logo from "../../assets/logo.png";
    import proIcon from "../../assets/user-icon.png";

    const dispatch = createEventDispatcher();

    export let showLogout = false;

    export let showSearch = false;

    let showMobSearch = false;

    let searchVal;

    let searchIconText = "refresh";

    let userInfo = {};
    let myTotalFeed;
    $: {
        userInfo = SessionUtil.get("info", true);
    }

    let menuItems = [
        {
            text: Labels.menu.profile,
            icon: "account_circle",
            action: "account",
        },
        {
            text: Labels.menu.home,
            icon: "menu_book",
            action: "home",
            selected: true,
        },
        {
            text: Labels.profile.my_post,
            icon: "dynamic_feed",
            action: "mypost",
        }
    ];
    export let selected;
    function onMenuItemClick(e, idx) {
        console.log("selected item : ", e, idx, menuItems[idx].action)

        dispatch("showview", {
            view: menuItems[idx].action,
        });
    }
    function setSelection() {
        console.log("getselection")
        let items = document.querySelectorAll("[item-action]");

        for (let i = 0; i < items.length; i++) {
            items[i].classList.remove("menu-selected");
        }

        let item = document.querySelector(`[item-action=${selected}]`);

        if (!Utils.isEmpty(item)) {
            item.classList.add("menu-selected");
        }

        let url = urlConst.get_user_posts.replace("{userId}", userInfo.userId);
        Request.get(url, null, onSuccess, onFailure, onSuccess);
    }
    function onSuccess(res) {
        myTotalFeed = (res['length'] < 2);
    }
    function onFailure(err) {
        Utils.log(err);
    }
    $: {
        if (selected) {
            setSelection();
        }
    }

    onMount(() => setSelection());
    function onLogout() {
        Utils.confirm(
            Labels.dashboard.logout_msg,
            Labels.alert.logout,
            function (btn) {
                if (btn === "ok") {
                    Utils.mask(true);
                    let data = { email: SessionUtil.get("info", true).email };
                    Request.post(
                        urlConst.logout + Utils.encodeForUrl(data),
                        data,
                        (resp) => {
                            Utils.mask(true);
                            SessionUtil.removeAll();
                            Utils.reload();
                        },
                        (err) => {
                            Utils.mask();
                            Utils.log("[Logout] Failed to logout");
                        },
                        onLogout
                    );
                }
            }
        );
    }

    function onSearchFeed(e) {
        dispatch("searchpost", searchVal);
    }

    function showSearchField() {
        showMobSearch = !showMobSearch;
    }

    function onHideSearch() {
        showMobSearch = false;
    }

    function omMobileSearch(e) {
        dispatch("searchpost", e.detail);
    }

    function takeBacktoHome() {
        Utils.redirectTo('home');
    }

    $: {
        if (searchVal) {
            searchIconText = "search";
        } else {
            searchIconText = "refresh";
        }
    }
</script>

<!-- <Toolbar cls="theme-bg"> -->
<Toolbar cls="theme-border-bottom">
    <!-- <div slot="left" class="flex-cont">
        {#if !Boot.isBigScreen()}
            <div class="flex-cont">
                <Button iconCls="material-icons" iconText="menu" />
            </div>
        {/if}
    </div> -->
    <div slot="center">
        {#if showSearch && !Boot.isMobile()}
            <!-- <div class="flex-cont flex-vh search-cont">
                <TextField
                    placeholder={Labels.header.search}
                    cls="search-feed"
                    bind:value={searchVal}
                    on:enter={onSearchFeed}
                />
                <Button
                    iconCls="material-icons"
                    iconText={searchIconText}
                    on:click={onSearchFeed}
                />
            </div> -->
        {/if}
    </div>
    <div slot="left">
        <!-- <img loading="lazy" src="http://rozmer.co.in/wp-content/uploads/2022/02/Rosmer-logo-final-14.png" alt=""> -->
        <!-- <div class="title">{Labels.app.name}</div> -->
        <!-- <div class="flex-cont" /> -->
        <img
            src={logo}
            alt={Labels.app.name}
            class="header-logo flex-cont vh-center pl-1"
            on:click={takeBacktoHome}
        />
    </div>

    <div slot="right" class="flex-cont">
        <div class="pro-card-cont">
            <div align="center" class="flex-cont">
                <!-- svelte-ignore a11y-missing-attribute -->
                <div class="m-auto"><span class="pro-card-user-name mr-2">Welcome ! {userInfo.firstName} {userInfo.lastName}</span></div>
                <img width="40px" src={proIcon} />
            </div>
        </div>
        <div class="ml-10 m-auto">
            <i class="fa fa-bell fa-lg pointer" style="color: #1a9b97;"></i>
        </div>
        <div class="ml-10 m-auto">
            <i class="fa fa-gear fa-lg pointer" style="color: #1a9b97;"></i>
        </div>
        <div class="ml-10 m-auto">
            <span class="flex-cont pointer" on:click={onLogout}>
                <div><span class="pro-card-user-name mr-2">Log out</span></div>
                <span class="material-icons" style="color: #1a9b97;">logout</span>
            </span>
        </div>
        {#if showSearch && Boot.isMobile()}
            <Button
                iconCls="material-icons"
                iconText="search"
                on:click={showSearchField}
            />
        {/if}
        {#if showLogout && !Boot.isBigScreen()}
            <!-- <Button
                iconCls="material-icons"
                iconText="publish"
                on:click={onLogout}
            /> -->
            <Button
                iconCls="material-icons"
                iconText="logout"
                on:click={onLogout}
            />
        {/if}
    </div>
</Toolbar>
<Toolbar cls="shadow-nav">
    <div slot="center">
        {#if showSearch && !Boot.isMobile()}

        {/if}
    </div>
    <div slot="left" class="flex-cont pl-1">
        <!-- <span class="material-icons" style="color: #1a9b97;">
            account_circle
            </span>
            <span class="material-icons" style="color: #1a9b97;">
                feed
                </span>
                <span class="material-icons menu-item-icon" style="color: #1a9b97;">dynamic_feed</span> -->
                {#each menuItems as item, idx}
            <div
                class="m-auto flex-cont mr-10 pointer pro-card-user-name {idx === 0 ? 'menu-selected' : ''}"
                data-index={idx}
                on:click={(event) => onMenuItemClick(event, idx)}
                item-action={item.action}
            >
                <span class="material-icons menu-item-icon mr-2">{item.icon}</span>
                <span class="font16 m-auto">{item.text}
                {#if item.text === Labels.menu.publish && myTotalFeed} 
                    *
                {/if}
                </span>
            </div>
        {/each}
            </div>

    <div slot="right" class="flex-cont">
        <!-- {#if showSearch && !Boot.isMobile()} -->
            <div class="flex-cont flex-vh search-cont">
                <TextField
                    placeholder={Labels.header.search}
                    cls="search-feed"
                    bind:value={searchVal}
                    on:enter={onSearchFeed}
                />
                <Button
                    iconCls="material-icons"
                    iconText={searchIconText}
                    on:click={onSearchFeed}
                />
            </div>
        <!-- {/if} -->
    </div>
</Toolbar>

{#if showSearch && showMobSearch && Boot.isMobile()}
    <MobileSearch
        on:hidesearch={onHideSearch}
        on:mobilesearch={omMobileSearch}
    />
{/if}

<style>
    :global(.search-cont .field-label) {
        display: none;
    }

    :global(.search-cont .btn-container) {
        padding: 0px;
        max-height: 36px;
    }

    :global(.search-cont .btn-container .btn-el) {
        padding: 4px 10px;
    }
    .pro-card-user-name {
        padding-top: 4px;
        font-size: 16px;
        font-weight: 500;
    }
</style>
