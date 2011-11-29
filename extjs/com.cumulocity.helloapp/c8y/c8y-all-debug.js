

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <title>
  m2m / cumulocity-platform-ui / source &mdash; Bitbucket
</title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <meta name="description" content="" />
  <meta name="keywords" content="" />
  <!--[if lt IE 9]>
  <script src="https://dwz7u9t8u8usb.cloudfront.net/m/8fca063a0549/js/lib/html5.js"></script>
  <![endif]-->

  <script>
    (function (window) {
      // prevent stray occurrences of `console.log` from causing errors in IE
      var console = window.console || (window.console = {});
      console.log || (console.log = function () {});

      var BB = window.BB || (window.BB = {});
      BB.debug = false;
      BB.cname = false;
      BB.CANON_URL = 'https://bitbucket.org';
      BB.MEDIA_URL = 'https://dwz7u9t8u8usb.cloudfront.net/m/8fca063a0549/';
      BB.images = {
        noAvatar: 'https://dwz7u9t8u8usb.cloudfront.net/m/8fca063a0549/img/no_avatar.png'
      };
      BB.user || (BB.user = {});
      BB.user.has = (function () {
        var betaFeatures = [];
        betaFeatures.push('repo2');
        return function (feature) {
          return _.contains(betaFeatures, feature);
        };
      }());
      BB.repo || (BB.repo = {});
  
      BB.user.follows = {
        repos: ''.split(',')
      };
      BB.user.id = 196483;
    
      BB.user.username = 'eickler';
    
      BB.user.isSshEnabled = true;
  
  
      BB.user.isAdmin = false;
      BB.repo.id = 378311;
    
    
      BB.repo.language = 'javascript';
      BB.repo.pygmentsLanguage = 'javascript';
    
    
      BB.repo.slug = 'cumulocity\u002Dplatform\u002Dui';
    
    
      BB.repo.owner = {
        username: 'm2m'
      };
    
      // Coerce `BB.repo` to a string to get
      // "davidchambers/mango" or whatever.
      BB.repo.toString = function () {
        return BB.cname ? this.slug : this.owner.username + '/' + this.slug;
      }
    
      BB.changeset = 'cee073ad5c39'
    
    
  
    }(this));
  </script>

  


  <link rel="stylesheet" href="https://dwz7u9t8u8usb.cloudfront.net/m/8fca063a0549/bun/css/bundle.css"/>



  <link rel="search" type="application/opensearchdescription+xml" href="/opensearch.xml" title="Bitbucket" />
  <link rel="icon" href="https://dwz7u9t8u8usb.cloudfront.net/m/8fca063a0549/img/logo_new.png" type="image/png" />
  <link type="text/plain" rel="author" href="/humans.txt" />


  
    <script src="https://dwz7u9t8u8usb.cloudfront.net/m/8fca063a0549/bun/js/bundle.js"></script>
  



</head>

<body id="" class="">
  <script type="text/javascript">
    if (!RegExp(" AppleWebKit/").test(navigator.userAgent)) {
    $('body').addClass('non-webkit');
    }
  </script>
  <!--[if IE 8]>
  <script>jQuery(document.body).addClass('ie8')</script>
  <![endif]-->
  <!--[if IE 9]>
  <script>jQuery(document.body).addClass('ie9')</script>
  <![endif]-->

  <div id="wrapper">



  <div id="header-wrap">
    <div id="header">
    <ul id="global-nav">
      <li><a class="home" href="http://www.atlassian.com">Atlassian Home</a></li>
      <li><a class="docs" href="http://confluence.atlassian.com/display/BITBUCKET">Documentation</a></li>
      <li><a class="support" href="/support">Support</a></li>
      <li><a class="blog" href="http://blog.bitbucket.org">Blog</a></li>
      <li><a class="forums" href="http://groups.google.com/group/bitbucket-users">Forums</a></li>
    </ul>
    <a href="/" id="logo">Bitbucket by Atlassian</a>

    <div id="main-nav">
    

      <ul class="clearfix">
        <li><a href="/explore" id="explore-link">Explore</a></li>
        <li><a href="https://bitbucket.org" id="dashboard-link">Dashboard</a></li>
        <li id="repositories-dropdown" class="inertial-hover active">
          <a class="drop-arrow" href="/repo/mine" id="repositories-link">Repositories</a>
          <div>
            <div>
              <div id="repo-overview"></div>
              <div class="group">
                <a href="/repo/create" class="new-repository" id="create-repo-link">Create repository</a>
                <a href="/repo/import" class="import-repository" id="import-repo-link">Import repository</a>
              </div>
            </div>
          </div>
        </li>
        <li id="user-dropdown" class="inertial-hover">
          <a class="drop-arrow" href="/eickler">
            <span>André Eickler</span>
          </a>
          <div>
            <div>
              <div class="group">
                <a href="/account/" id="account-link">Account</a>
                <a href="/account/notifications/" id="inbox-link">Inbox <span>(6)</span></a>
              </div>
              <div class="group">
                <a href="/account/signout/">Log out</a>
              </div>
            </div>
          </div>
        </li>
        

<li class="search-box">
  
    <form action="/repo/all">
      <input type="search" results="5" autosave="bitbucket-explore-search"
             name="name" id="searchbox"
             placeholder="Find a project" />
  
  </form>
</li>

      </ul>

    
    </div>
    </div>
  </div>

    <div id="header-messages">
  
  
    
    
    
    
  

    
   </div>



    <div id="content">
      <div id="source">
      
  
  





  <script>
    jQuery(function ($) {
        var cookie = $.cookie,
            cookieOptions, date,
            $content = $('#content'),
            $pane = $('#what-is-bitbucket'),
            $hide = $pane.find('[href="#hide"]').css('display', 'block').hide();

        date = new Date();
        date.setTime(date.getTime() + 365 * 24 * 60 * 60 * 1000);
        cookieOptions = { path: '/', expires: date };

        if (cookie('toggle_status') == 'hide') $content.addClass('repo-desc-hidden');

        $('#toggle-repo-content').click(function (event) {
            event.preventDefault();
            $content.toggleClass('repo-desc-hidden');
            cookie('toggle_status', cookie('toggle_status') == 'show' ? 'hide' : 'show', cookieOptions);
        });

        if (!cookie('hide_intro_message')) $pane.show();

        $hide.click(function (event) {
            event.preventDefault();
            cookie('hide_intro_message', true, cookieOptions);
            $pane.slideUp('slow');
        });

        $pane.hover(
            function () { $hide.fadeIn('fast'); },
            function () { $hide.fadeOut('fast'); });

      (function () {
        // Update "recently-viewed-repos" cookie for
        // the "repositories" drop-down.
        var
          id = BB.repo.id,
          cookieName = 'recently-viewed-repos_' + BB.user.id,
          rvr = cookie(cookieName),
          ids = rvr? rvr.split(','): [],
          idx = _.indexOf(ids, '' + id);

        // Remove `id` from `ids` if present.
        if (~idx) ids.splice(idx, 1);

        cookie(
          cookieName,
          // Insert `id` as the first item, then call
          // `join` on the resulting array to produce
          // something like "114694,27542,89002,84570".
          [id].concat(ids.slice(0, 4)).join(),
          {path: '/', expires: 1e6} // "never" expires
        );
      }());
    });
  </script>




<div id="tabs">
  <ul class="tabs">
    <li>
      <a href="/m2m/cumulocity-platform-ui/overview" id="repo-overview-link">Overview</a>
    </li>

    <li>
      <a href="/m2m/cumulocity-platform-ui/downloads" id="repo-downloads-link">Downloads (<span id="downloads-count">0</span>)</a>
    </li>

    

    <li>
      <a href="/m2m/cumulocity-platform-ui/pull-requests" id="repo-pr-link">Pull requests (0)</a>
    </li>

    <li class="selected">
      
        <a href="/m2m/cumulocity-platform-ui/src" id="repo-source-link">Source</a>
      
    </li>

    <li>
      <a href="/m2m/cumulocity-platform-ui/changesets" id="repo-commits-link">Commits</a>
    </li>

    <li id="wiki-tab" class="dropdown"
      style="display:
                        none  
        
      ">
      <a href="/m2m/cumulocity-platform-ui/wiki" id="repo-wiki-link">Wiki</a>
    </li>

    <li id="issues-tab" class="dropdown inertial-hover"
      style="display:
                      none  
        
      ">
      <a href="/m2m/cumulocity-platform-ui/issues?status=new&amp;status=open" id="repo-issues-link">Issues (0) &raquo;</a>
      <ul>
        <li><a href="/m2m/cumulocity-platform-ui/issues/new">Create new issue</a></li>
        <li><a href="/m2m/cumulocity-platform-ui/issues?status=new">New issues</a></li>
        <li><a href="/m2m/cumulocity-platform-ui/issues?status=new&amp;status=open">Open issues</a></li>
        <li><a href="/m2m/cumulocity-platform-ui/issues?status=duplicate&amp;status=invalid&amp;status=resolved&amp;status=wontfix">Closed issues</a></li>
        
          <li><a href="/m2m/cumulocity-platform-ui/issues?responsible=eickler">My issues</a></li>
        
        <li><a href="/m2m/cumulocity-platform-ui/issues">All issues</a></li>
        <li><a href="/m2m/cumulocity-platform-ui/issues/query">Advanced query</a></li>
      </ul>
    </li>

    

    <li class="secondary">
      <a href="/m2m/cumulocity-platform-ui/descendants">Forks/queues (0)</a>
    </li>

    <li class="secondary">
      <a href="/m2m/cumulocity-platform-ui/zealots">Followers (<span id="followers-count">3</span>)</a>
    </li>
  </ul>
</div>



 

  <div class="repo-menu" id="repo-menu">
    <ul id="repo-menu-links">
    
      <li>
        <a href="/m2m/cumulocity-platform-ui/rss?token=2d4d5110c13c4c60e3e28e4ab7cd1471" class="rss" title="RSS feed for cumulocity-platform-ui">RSS</a>
      </li>

      <li><a href="/m2m/cumulocity-platform-ui/fork" class="fork">fork</a></li>
      
        
          <li><a href="/m2m/cumulocity-platform-ui/hack" class="patch-queue">patch queue</a></li>
        
      
      <li>
        <a rel="nofollow" href="/m2m/cumulocity-platform-ui/follow" class="follow">follow</a>
      </li>
      
          
            <li>
              <a href="/account/notifications/send/?receiver=m2m&amp;subject=revoke%20my%20write%20access%20to%20m2m/cumulocity-platform-ui&amp;message=Currently%20I%20have%20access%20to%20the%20m2m/cumulocity-platform-ui%20via%20the%20group%20m2m%3Adevelopers.%0A%0ACould%20you%20please%20revoke%20my%20write%20access%20to%20the%20repository%20m2m/cumulocity-platform-ui%3F" class="revoke-group" title="Your access to this repository is via a group. To remove your access, you will need to contact the group owner.">revoke</a>
            </li>
          
      
      
        <li class="get-source inertial-hover">
          <a class="source">get source</a>
          <ul class="downloads">
            
              
              <li><a rel="nofollow" href="/m2m/cumulocity-platform-ui/get/cee073ad5c39.zip">zip</a></li>
              <li><a rel="nofollow" href="/m2m/cumulocity-platform-ui/get/cee073ad5c39.tar.gz">gz</a></li>
              <li><a rel="nofollow" href="/m2m/cumulocity-platform-ui/get/cee073ad5c39.tar.bz2">bz2</a></li>
            
          </ul>
        </li>
      
    </ul>

  
    <ul class="metadata">
      
      
        <li class="branches inertial-hover">branches
          <ul>
            <li><a href="/m2m/cumulocity-platform-ui/src/aeeb1099bfb6">build</a>
              
              
              <a rel="nofollow" class="menu-compare"
                 href="/m2m/cumulocity-platform-ui/compare/build..default"
                 title="Show changes between build and the main branch.">compare</a>
              
            </li>
            <li><a href="/m2m/cumulocity-platform-ui/src/e010153b0268">default</a>
              
              
            </li>
          </ul>
        </li>
      
      
      <li class="tags inertial-hover">tags
        <ul>
          <li><a href="/m2m/cumulocity-platform-ui/src/e010153b0268">tip</a>
            
            </li>
          <li><a href="/m2m/cumulocity-platform-ui/src/4745070bec53">ui-0.5.0</a>
            
            
              <a rel="nofollow" class='menu-compare'
                 href="/m2m/cumulocity-platform-ui/compare/..ui-0.5.0"
                 title="Show changes between ui-0.5.0 and the main branch.">compare</a>
            </li>
        </ul>
      </li>
     
     
    </ul>
  
</div>

<div class="repo-menu" id="repo-desc">
  
    <ul id="repo-menu-links-mini">
      <li><a rel="nofollow" class="compare-link"
             href="/m2m/cumulocity-platform-ui/compare/../"
             title="Show changes between cumulocity-platform-ui and "
             ></a></li>
      
  

      
      <li>
        <a href="/m2m/cumulocity-platform-ui/rss?token=2d4d5110c13c4c60e3e28e4ab7cd1471" class="rss" title="RSS feed for cumulocity-platform-ui"></a>
      </li>

      <li><a href="/m2m/cumulocity-platform-ui/fork" class="fork" title="Fork"></a></li>
      
        
          <li><a href="/m2m/cumulocity-platform-ui/hack" class="patch-queue" title="Patch queue"></a></li>
        
      
      <li>
        <a rel="nofollow" href="/m2m/cumulocity-platform-ui/follow" class="follow">follow</a>
      </li>
      
          
            <li>
              <a href="/account/notifications/send/?receiver=m2m&amp;subject=revoke%20my%20write%20access%20to%20m2m/cumulocity-platform-ui&amp;message=Currently%20I%20have%20access%20to%20the%20m2m/cumulocity-platform-ui%20via%20the%20group%20m2m%3Adevelopers.%0A%0ACould%20you%20please%20revoke%20my%20write%20access%20to%20the%20repository%20m2m/cumulocity-platform-ui%3F" class="revoke-group" title="Your access to this repository is via a group. To remove your access, you will need to contact the group owner.">revoke</a>
            </li>
          
      
    
      
        <li>
          <a class="source" title="Get source"></a>
          <ul class="downloads">
            
              
                <li><a rel="nofollow" href="/m2m/cumulocity-platform-ui/get/e010153b0268.zip">zip</a></li>
                <li><a rel="nofollow" href="/m2m/cumulocity-platform-ui/get/e010153b0268.tar.gz">gz</a></li>
                <li><a rel="nofollow" href="/m2m/cumulocity-platform-ui/get/e010153b0268.tar.bz2">bz2</a></li>
              
            
          </ul>
        </li>
      
    
    </ul>

    <h3 id="repo-heading" class="private hg">
      <a class="owner-username" href="/m2m">m2m</a> /
      <a class="repo-name" href="/m2m/cumulocity-platform-ui">cumulocity-platform-ui</a>
    

    
    </h3>

        

  <div id="repo-desc-cloneinfo">Clone this repository (size: 86.4 MB):
    <a href="https://eickler@bitbucket.org/m2m/cumulocity-platform-ui" class="https">HTTPS</a> /
    <a href="ssh://hg@bitbucket.org/m2m/cumulocity-platform-ui" class="ssh">SSH</a>
    <pre id="clone-url-https">hg clone https://eickler@bitbucket.org/m2m/cumulocity-platform-ui</pre>
    <pre id="clone-url-ssh">hg clone ssh://hg@bitbucket.org/m2m/cumulocity-platform-ui</pre>
    
  </div>

        <a href="#" id="toggle-repo-content"></a>

        

</div>




      
  <div id="source-container">
    

  <div id="source-path">
    <h1>
      <a href="/m2m/cumulocity-platform-ui/src" class="src-pjax">cumulocity-platform-ui</a> /

  
    
      <a href="/m2m/cumulocity-platform-ui/src/cee073ad5c39/build/" class="src-pjax">build</a> /
    
  

  
    
      <a href="/m2m/cumulocity-platform-ui/src/cee073ad5c39/build/src/" class="src-pjax">src</a> /
    
  

  
    
      <a href="/m2m/cumulocity-platform-ui/src/cee073ad5c39/build/src/main/" class="src-pjax">main</a> /
    
  

  
    
      <a href="/m2m/cumulocity-platform-ui/src/cee073ad5c39/build/src/main/webapp/" class="src-pjax">webapp</a> /
    
  

  
    
      <a href="/m2m/cumulocity-platform-ui/src/cee073ad5c39/build/src/main/webapp/c8y/" class="src-pjax">c8y</a> /
    
  

  
    
      c8y-all-debug.js
    
  

    </h1>
  </div>

  <div class="labels labels-csv">
  
    <dl>
  
    
  
  
    
  
  
</dl>

  
  </div>


  
  <div id="source-view">
    <div class="header">
      <ul class="metadata">
        <li><code>cee073ad5c39</code></li>
        
          
            <li>4001 loc</li>
          
        
        <li>116.4 KB</li>
      </ul>
      <ul class="source-view-links">
        
        <li><a href="/m2m/cumulocity-platform-ui/history/build/src/main/webapp/c8y/c8y-all-debug.js">history</a></li>
        
        <li><a href="/m2m/cumulocity-platform-ui/annotate/cee073ad5c39/build/src/main/webapp/c8y/c8y-all-debug.js">annotate</a></li>
        
        <li><a href="/m2m/cumulocity-platform-ui/raw/cee073ad5c39/build/src/main/webapp/c8y/c8y-all-debug.js">raw</a></li>
        <li>
          <form action="/m2m/cumulocity-platform-ui/diff/build/src/main/webapp/c8y/c8y-all-debug.js" class="source-view-form">
          
            <input type="hidden" name="diff2" value="cee073ad5c39" />
            <select name="diff1">
            
              
            
            </select>
            <input type="submit" value="diff" />
          
          </form>
        </li>
      </ul>
    </div>
  
    <div>
    <table class="highlighttable">
<tr><td class="linenos"><div class="linenodiv"><pre>
<a href="#cl-1">1</a>
<a href="#cl-2">2</a>
<a href="#cl-3">3</a>
<a href="#cl-4">4</a>
<a href="#cl-5">5</a>
<a href="#cl-6">6</a>
<a href="#cl-7">7</a>
<a href="#cl-8">8</a>
<a href="#cl-9">9</a>
<a href="#cl-10">10</a>
<a href="#cl-11">11</a>
<a href="#cl-12">12</a>
<a href="#cl-13">13</a>
<a href="#cl-14">14</a>
<a href="#cl-15">15</a>
<a href="#cl-16">16</a>
<a href="#cl-17">17</a>
<a href="#cl-18">18</a>
<a href="#cl-19">19</a>
<a href="#cl-20">20</a>
<a href="#cl-21">21</a>
<a href="#cl-22">22</a>
<a href="#cl-23">23</a>
<a href="#cl-24">24</a>
<a href="#cl-25">25</a>
<a href="#cl-26">26</a>
<a href="#cl-27">27</a>
<a href="#cl-28">28</a>
<a href="#cl-29">29</a>
<a href="#cl-30">30</a>
<a href="#cl-31">31</a>
<a href="#cl-32">32</a>
<a href="#cl-33">33</a>
<a href="#cl-34">34</a>
<a href="#cl-35">35</a>
<a href="#cl-36">36</a>
<a href="#cl-37">37</a>
<a href="#cl-38">38</a>
<a href="#cl-39">39</a>
<a href="#cl-40">40</a>
<a href="#cl-41">41</a>
<a href="#cl-42">42</a>
<a href="#cl-43">43</a>
<a href="#cl-44">44</a>
<a href="#cl-45">45</a>
<a href="#cl-46">46</a>
<a href="#cl-47">47</a>
<a href="#cl-48">48</a>
<a href="#cl-49">49</a>
<a href="#cl-50">50</a>
<a href="#cl-51">51</a>
<a href="#cl-52">52</a>
<a href="#cl-53">53</a>
<a href="#cl-54">54</a>
<a href="#cl-55">55</a>
<a href="#cl-56">56</a>
<a href="#cl-57">57</a>
<a href="#cl-58">58</a>
<a href="#cl-59">59</a>
<a href="#cl-60">60</a>
<a href="#cl-61">61</a>
<a href="#cl-62">62</a>
<a href="#cl-63">63</a>
<a href="#cl-64">64</a>
<a href="#cl-65">65</a>
<a href="#cl-66">66</a>
<a href="#cl-67">67</a>
<a href="#cl-68">68</a>
<a href="#cl-69">69</a>
<a href="#cl-70">70</a>
<a href="#cl-71">71</a>
<a href="#cl-72">72</a>
<a href="#cl-73">73</a>
<a href="#cl-74">74</a>
<a href="#cl-75">75</a>
<a href="#cl-76">76</a>
<a href="#cl-77">77</a>
<a href="#cl-78">78</a>
<a href="#cl-79">79</a>
<a href="#cl-80">80</a>
<a href="#cl-81">81</a>
<a href="#cl-82">82</a>
<a href="#cl-83">83</a>
<a href="#cl-84">84</a>
<a href="#cl-85">85</a>
<a href="#cl-86">86</a>
<a href="#cl-87">87</a>
<a href="#cl-88">88</a>
<a href="#cl-89">89</a>
<a href="#cl-90">90</a>
<a href="#cl-91">91</a>
<a href="#cl-92">92</a>
<a href="#cl-93">93</a>
<a href="#cl-94">94</a>
<a href="#cl-95">95</a>
<a href="#cl-96">96</a>
<a href="#cl-97">97</a>
<a href="#cl-98">98</a>
<a href="#cl-99">99</a>
<a href="#cl-100">100</a>
<a href="#cl-101">101</a>
<a href="#cl-102">102</a>
<a href="#cl-103">103</a>
<a href="#cl-104">104</a>
<a href="#cl-105">105</a>
<a href="#cl-106">106</a>
<a href="#cl-107">107</a>
<a href="#cl-108">108</a>
<a href="#cl-109">109</a>
<a href="#cl-110">110</a>
<a href="#cl-111">111</a>
<a href="#cl-112">112</a>
<a href="#cl-113">113</a>
<a href="#cl-114">114</a>
<a href="#cl-115">115</a>
<a href="#cl-116">116</a>
<a href="#cl-117">117</a>
<a href="#cl-118">118</a>
<a href="#cl-119">119</a>
<a href="#cl-120">120</a>
<a href="#cl-121">121</a>
<a href="#cl-122">122</a>
<a href="#cl-123">123</a>
<a href="#cl-124">124</a>
<a href="#cl-125">125</a>
<a href="#cl-126">126</a>
<a href="#cl-127">127</a>
<a href="#cl-128">128</a>
<a href="#cl-129">129</a>
<a href="#cl-130">130</a>
<a href="#cl-131">131</a>
<a href="#cl-132">132</a>
<a href="#cl-133">133</a>
<a href="#cl-134">134</a>
<a href="#cl-135">135</a>
<a href="#cl-136">136</a>
<a href="#cl-137">137</a>
<a href="#cl-138">138</a>
<a href="#cl-139">139</a>
<a href="#cl-140">140</a>
<a href="#cl-141">141</a>
<a href="#cl-142">142</a>
<a href="#cl-143">143</a>
<a href="#cl-144">144</a>
<a href="#cl-145">145</a>
<a href="#cl-146">146</a>
<a href="#cl-147">147</a>
<a href="#cl-148">148</a>
<a href="#cl-149">149</a>
<a href="#cl-150">150</a>
<a href="#cl-151">151</a>
<a href="#cl-152">152</a>
<a href="#cl-153">153</a>
<a href="#cl-154">154</a>
<a href="#cl-155">155</a>
<a href="#cl-156">156</a>
<a href="#cl-157">157</a>
<a href="#cl-158">158</a>
<a href="#cl-159">159</a>
<a href="#cl-160">160</a>
<a href="#cl-161">161</a>
<a href="#cl-162">162</a>
<a href="#cl-163">163</a>
<a href="#cl-164">164</a>
<a href="#cl-165">165</a>
<a href="#cl-166">166</a>
<a href="#cl-167">167</a>
<a href="#cl-168">168</a>
<a href="#cl-169">169</a>
<a href="#cl-170">170</a>
<a href="#cl-171">171</a>
<a href="#cl-172">172</a>
<a href="#cl-173">173</a>
<a href="#cl-174">174</a>
<a href="#cl-175">175</a>
<a href="#cl-176">176</a>
<a href="#cl-177">177</a>
<a href="#cl-178">178</a>
<a href="#cl-179">179</a>
<a href="#cl-180">180</a>
<a href="#cl-181">181</a>
<a href="#cl-182">182</a>
<a href="#cl-183">183</a>
<a href="#cl-184">184</a>
<a href="#cl-185">185</a>
<a href="#cl-186">186</a>
<a href="#cl-187">187</a>
<a href="#cl-188">188</a>
<a href="#cl-189">189</a>
<a href="#cl-190">190</a>
<a href="#cl-191">191</a>
<a href="#cl-192">192</a>
<a href="#cl-193">193</a>
<a href="#cl-194">194</a>
<a href="#cl-195">195</a>
<a href="#cl-196">196</a>
<a href="#cl-197">197</a>
<a href="#cl-198">198</a>
<a href="#cl-199">199</a>
<a href="#cl-200">200</a>
<a href="#cl-201">201</a>
<a href="#cl-202">202</a>
<a href="#cl-203">203</a>
<a href="#cl-204">204</a>
<a href="#cl-205">205</a>
<a href="#cl-206">206</a>
<a href="#cl-207">207</a>
<a href="#cl-208">208</a>
<a href="#cl-209">209</a>
<a href="#cl-210">210</a>
<a href="#cl-211">211</a>
<a href="#cl-212">212</a>
<a href="#cl-213">213</a>
<a href="#cl-214">214</a>
<a href="#cl-215">215</a>
<a href="#cl-216">216</a>
<a href="#cl-217">217</a>
<a href="#cl-218">218</a>
<a href="#cl-219">219</a>
<a href="#cl-220">220</a>
<a href="#cl-221">221</a>
<a href="#cl-222">222</a>
<a href="#cl-223">223</a>
<a href="#cl-224">224</a>
<a href="#cl-225">225</a>
<a href="#cl-226">226</a>
<a href="#cl-227">227</a>
<a href="#cl-228">228</a>
<a href="#cl-229">229</a>
<a href="#cl-230">230</a>
<a href="#cl-231">231</a>
<a href="#cl-232">232</a>
<a href="#cl-233">233</a>
<a href="#cl-234">234</a>
<a href="#cl-235">235</a>
<a href="#cl-236">236</a>
<a href="#cl-237">237</a>
<a href="#cl-238">238</a>
<a href="#cl-239">239</a>
<a href="#cl-240">240</a>
<a href="#cl-241">241</a>
<a href="#cl-242">242</a>
<a href="#cl-243">243</a>
<a href="#cl-244">244</a>
<a href="#cl-245">245</a>
<a href="#cl-246">246</a>
<a href="#cl-247">247</a>
<a href="#cl-248">248</a>
<a href="#cl-249">249</a>
<a href="#cl-250">250</a>
<a href="#cl-251">251</a>
<a href="#cl-252">252</a>
<a href="#cl-253">253</a>
<a href="#cl-254">254</a>
<a href="#cl-255">255</a>
<a href="#cl-256">256</a>
<a href="#cl-257">257</a>
<a href="#cl-258">258</a>
<a href="#cl-259">259</a>
<a href="#cl-260">260</a>
<a href="#cl-261">261</a>
<a href="#cl-262">262</a>
<a href="#cl-263">263</a>
<a href="#cl-264">264</a>
<a href="#cl-265">265</a>
<a href="#cl-266">266</a>
<a href="#cl-267">267</a>
<a href="#cl-268">268</a>
<a href="#cl-269">269</a>
<a href="#cl-270">270</a>
<a href="#cl-271">271</a>
<a href="#cl-272">272</a>
<a href="#cl-273">273</a>
<a href="#cl-274">274</a>
<a href="#cl-275">275</a>
<a href="#cl-276">276</a>
<a href="#cl-277">277</a>
<a href="#cl-278">278</a>
<a href="#cl-279">279</a>
<a href="#cl-280">280</a>
<a href="#cl-281">281</a>
<a href="#cl-282">282</a>
<a href="#cl-283">283</a>
<a href="#cl-284">284</a>
<a href="#cl-285">285</a>
<a href="#cl-286">286</a>
<a href="#cl-287">287</a>
<a href="#cl-288">288</a>
<a href="#cl-289">289</a>
<a href="#cl-290">290</a>
<a href="#cl-291">291</a>
<a href="#cl-292">292</a>
<a href="#cl-293">293</a>
<a href="#cl-294">294</a>
<a href="#cl-295">295</a>
<a href="#cl-296">296</a>
<a href="#cl-297">297</a>
<a href="#cl-298">298</a>
<a href="#cl-299">299</a>
<a href="#cl-300">300</a>
<a href="#cl-301">301</a>
<a href="#cl-302">302</a>
<a href="#cl-303">303</a>
<a href="#cl-304">304</a>
<a href="#cl-305">305</a>
<a href="#cl-306">306</a>
<a href="#cl-307">307</a>
<a href="#cl-308">308</a>
<a href="#cl-309">309</a>
<a href="#cl-310">310</a>
<a href="#cl-311">311</a>
<a href="#cl-312">312</a>
<a href="#cl-313">313</a>
<a href="#cl-314">314</a>
<a href="#cl-315">315</a>
<a href="#cl-316">316</a>
<a href="#cl-317">317</a>
<a href="#cl-318">318</a>
<a href="#cl-319">319</a>
<a href="#cl-320">320</a>
<a href="#cl-321">321</a>
<a href="#cl-322">322</a>
<a href="#cl-323">323</a>
<a href="#cl-324">324</a>
<a href="#cl-325">325</a>
<a href="#cl-326">326</a>
<a href="#cl-327">327</a>
<a href="#cl-328">328</a>
<a href="#cl-329">329</a>
<a href="#cl-330">330</a>
<a href="#cl-331">331</a>
<a href="#cl-332">332</a>
<a href="#cl-333">333</a>
<a href="#cl-334">334</a>
<a href="#cl-335">335</a>
<a href="#cl-336">336</a>
<a href="#cl-337">337</a>
<a href="#cl-338">338</a>
<a href="#cl-339">339</a>
<a href="#cl-340">340</a>
<a href="#cl-341">341</a>
<a href="#cl-342">342</a>
<a href="#cl-343">343</a>
<a href="#cl-344">344</a>
<a href="#cl-345">345</a>
<a href="#cl-346">346</a>
<a href="#cl-347">347</a>
<a href="#cl-348">348</a>
<a href="#cl-349">349</a>
<a href="#cl-350">350</a>
<a href="#cl-351">351</a>
<a href="#cl-352">352</a>
<a href="#cl-353">353</a>
<a href="#cl-354">354</a>
<a href="#cl-355">355</a>
<a href="#cl-356">356</a>
<a href="#cl-357">357</a>
<a href="#cl-358">358</a>
<a href="#cl-359">359</a>
<a href="#cl-360">360</a>
<a href="#cl-361">361</a>
<a href="#cl-362">362</a>
<a href="#cl-363">363</a>
<a href="#cl-364">364</a>
<a href="#cl-365">365</a>
<a href="#cl-366">366</a>
<a href="#cl-367">367</a>
<a href="#cl-368">368</a>
<a href="#cl-369">369</a>
<a href="#cl-370">370</a>
<a href="#cl-371">371</a>
<a href="#cl-372">372</a>
<a href="#cl-373">373</a>
<a href="#cl-374">374</a>
<a href="#cl-375">375</a>
<a href="#cl-376">376</a>
<a href="#cl-377">377</a>
<a href="#cl-378">378</a>
<a href="#cl-379">379</a>
<a href="#cl-380">380</a>
<a href="#cl-381">381</a>
<a href="#cl-382">382</a>
<a href="#cl-383">383</a>
<a href="#cl-384">384</a>
<a href="#cl-385">385</a>
<a href="#cl-386">386</a>
<a href="#cl-387">387</a>
<a href="#cl-388">388</a>
<a href="#cl-389">389</a>
<a href="#cl-390">390</a>
<a href="#cl-391">391</a>
<a href="#cl-392">392</a>
<a href="#cl-393">393</a>
<a href="#cl-394">394</a>
<a href="#cl-395">395</a>
<a href="#cl-396">396</a>
<a href="#cl-397">397</a>
<a href="#cl-398">398</a>
<a href="#cl-399">399</a>
<a href="#cl-400">400</a>
<a href="#cl-401">401</a>
<a href="#cl-402">402</a>
<a href="#cl-403">403</a>
<a href="#cl-404">404</a>
<a href="#cl-405">405</a>
<a href="#cl-406">406</a>
<a href="#cl-407">407</a>
<a href="#cl-408">408</a>
<a href="#cl-409">409</a>
<a href="#cl-410">410</a>
<a href="#cl-411">411</a>
<a href="#cl-412">412</a>
<a href="#cl-413">413</a>
<a href="#cl-414">414</a>
<a href="#cl-415">415</a>
<a href="#cl-416">416</a>
<a href="#cl-417">417</a>
<a href="#cl-418">418</a>
<a href="#cl-419">419</a>
<a href="#cl-420">420</a>
<a href="#cl-421">421</a>
<a href="#cl-422">422</a>
<a href="#cl-423">423</a>
<a href="#cl-424">424</a>
<a href="#cl-425">425</a>
<a href="#cl-426">426</a>
<a href="#cl-427">427</a>
<a href="#cl-428">428</a>
<a href="#cl-429">429</a>
<a href="#cl-430">430</a>
<a href="#cl-431">431</a>
<a href="#cl-432">432</a>
<a href="#cl-433">433</a>
<a href="#cl-434">434</a>
<a href="#cl-435">435</a>
<a href="#cl-436">436</a>
<a href="#cl-437">437</a>
<a href="#cl-438">438</a>
<a href="#cl-439">439</a>
<a href="#cl-440">440</a>
<a href="#cl-441">441</a>
<a href="#cl-442">442</a>
<a href="#cl-443">443</a>
<a href="#cl-444">444</a>
<a href="#cl-445">445</a>
<a href="#cl-446">446</a>
<a href="#cl-447">447</a>
<a href="#cl-448">448</a>
<a href="#cl-449">449</a>
<a href="#cl-450">450</a>
<a href="#cl-451">451</a>
<a href="#cl-452">452</a>
<a href="#cl-453">453</a>
<a href="#cl-454">454</a>
<a href="#cl-455">455</a>
<a href="#cl-456">456</a>
<a href="#cl-457">457</a>
<a href="#cl-458">458</a>
<a href="#cl-459">459</a>
<a href="#cl-460">460</a>
<a href="#cl-461">461</a>
<a href="#cl-462">462</a>
<a href="#cl-463">463</a>
<a href="#cl-464">464</a>
<a href="#cl-465">465</a>
<a href="#cl-466">466</a>
<a href="#cl-467">467</a>
<a href="#cl-468">468</a>
<a href="#cl-469">469</a>
<a href="#cl-470">470</a>
<a href="#cl-471">471</a>
<a href="#cl-472">472</a>
<a href="#cl-473">473</a>
<a href="#cl-474">474</a>
<a href="#cl-475">475</a>
<a href="#cl-476">476</a>
<a href="#cl-477">477</a>
<a href="#cl-478">478</a>
<a href="#cl-479">479</a>
<a href="#cl-480">480</a>
<a href="#cl-481">481</a>
<a href="#cl-482">482</a>
<a href="#cl-483">483</a>
<a href="#cl-484">484</a>
<a href="#cl-485">485</a>
<a href="#cl-486">486</a>
<a href="#cl-487">487</a>
<a href="#cl-488">488</a>
<a href="#cl-489">489</a>
<a href="#cl-490">490</a>
<a href="#cl-491">491</a>
<a href="#cl-492">492</a>
<a href="#cl-493">493</a>
<a href="#cl-494">494</a>
<a href="#cl-495">495</a>
<a href="#cl-496">496</a>
<a href="#cl-497">497</a>
<a href="#cl-498">498</a>
<a href="#cl-499">499</a>
<a href="#cl-500">500</a>
<a href="#cl-501">501</a>
<a href="#cl-502">502</a>
<a href="#cl-503">503</a>
<a href="#cl-504">504</a>
<a href="#cl-505">505</a>
<a href="#cl-506">506</a>
<a href="#cl-507">507</a>
<a href="#cl-508">508</a>
<a href="#cl-509">509</a>
<a href="#cl-510">510</a>
<a href="#cl-511">511</a>
<a href="#cl-512">512</a>
<a href="#cl-513">513</a>
<a href="#cl-514">514</a>
<a href="#cl-515">515</a>
<a href="#cl-516">516</a>
<a href="#cl-517">517</a>
<a href="#cl-518">518</a>
<a href="#cl-519">519</a>
<a href="#cl-520">520</a>
<a href="#cl-521">521</a>
<a href="#cl-522">522</a>
<a href="#cl-523">523</a>
<a href="#cl-524">524</a>
<a href="#cl-525">525</a>
<a href="#cl-526">526</a>
<a href="#cl-527">527</a>
<a href="#cl-528">528</a>
<a href="#cl-529">529</a>
<a href="#cl-530">530</a>
<a href="#cl-531">531</a>
<a href="#cl-532">532</a>
<a href="#cl-533">533</a>
<a href="#cl-534">534</a>
<a href="#cl-535">535</a>
<a href="#cl-536">536</a>
<a href="#cl-537">537</a>
<a href="#cl-538">538</a>
<a href="#cl-539">539</a>
<a href="#cl-540">540</a>
<a href="#cl-541">541</a>
<a href="#cl-542">542</a>
<a href="#cl-543">543</a>
<a href="#cl-544">544</a>
<a href="#cl-545">545</a>
<a href="#cl-546">546</a>
<a href="#cl-547">547</a>
<a href="#cl-548">548</a>
<a href="#cl-549">549</a>
<a href="#cl-550">550</a>
<a href="#cl-551">551</a>
<a href="#cl-552">552</a>
<a href="#cl-553">553</a>
<a href="#cl-554">554</a>
<a href="#cl-555">555</a>
<a href="#cl-556">556</a>
<a href="#cl-557">557</a>
<a href="#cl-558">558</a>
<a href="#cl-559">559</a>
<a href="#cl-560">560</a>
<a href="#cl-561">561</a>
<a href="#cl-562">562</a>
<a href="#cl-563">563</a>
<a href="#cl-564">564</a>
<a href="#cl-565">565</a>
<a href="#cl-566">566</a>
<a href="#cl-567">567</a>
<a href="#cl-568">568</a>
<a href="#cl-569">569</a>
<a href="#cl-570">570</a>
<a href="#cl-571">571</a>
<a href="#cl-572">572</a>
<a href="#cl-573">573</a>
<a href="#cl-574">574</a>
<a href="#cl-575">575</a>
<a href="#cl-576">576</a>
<a href="#cl-577">577</a>
<a href="#cl-578">578</a>
<a href="#cl-579">579</a>
<a href="#cl-580">580</a>
<a href="#cl-581">581</a>
<a href="#cl-582">582</a>
<a href="#cl-583">583</a>
<a href="#cl-584">584</a>
<a href="#cl-585">585</a>
<a href="#cl-586">586</a>
<a href="#cl-587">587</a>
<a href="#cl-588">588</a>
<a href="#cl-589">589</a>
<a href="#cl-590">590</a>
<a href="#cl-591">591</a>
<a href="#cl-592">592</a>
<a href="#cl-593">593</a>
<a href="#cl-594">594</a>
<a href="#cl-595">595</a>
<a href="#cl-596">596</a>
<a href="#cl-597">597</a>
<a href="#cl-598">598</a>
<a href="#cl-599">599</a>
<a href="#cl-600">600</a>
<a href="#cl-601">601</a>
<a href="#cl-602">602</a>
<a href="#cl-603">603</a>
<a href="#cl-604">604</a>
<a href="#cl-605">605</a>
<a href="#cl-606">606</a>
<a href="#cl-607">607</a>
<a href="#cl-608">608</a>
<a href="#cl-609">609</a>
<a href="#cl-610">610</a>
<a href="#cl-611">611</a>
<a href="#cl-612">612</a>
<a href="#cl-613">613</a>
<a href="#cl-614">614</a>
<a href="#cl-615">615</a>
<a href="#cl-616">616</a>
<a href="#cl-617">617</a>
<a href="#cl-618">618</a>
<a href="#cl-619">619</a>
<a href="#cl-620">620</a>
<a href="#cl-621">621</a>
<a href="#cl-622">622</a>
<a href="#cl-623">623</a>
<a href="#cl-624">624</a>
<a href="#cl-625">625</a>
<a href="#cl-626">626</a>
<a href="#cl-627">627</a>
<a href="#cl-628">628</a>
<a href="#cl-629">629</a>
<a href="#cl-630">630</a>
<a href="#cl-631">631</a>
<a href="#cl-632">632</a>
<a href="#cl-633">633</a>
<a href="#cl-634">634</a>
<a href="#cl-635">635</a>
<a href="#cl-636">636</a>
<a href="#cl-637">637</a>
<a href="#cl-638">638</a>
<a href="#cl-639">639</a>
<a href="#cl-640">640</a>
<a href="#cl-641">641</a>
<a href="#cl-642">642</a>
<a href="#cl-643">643</a>
<a href="#cl-644">644</a>
<a href="#cl-645">645</a>
<a href="#cl-646">646</a>
<a href="#cl-647">647</a>
<a href="#cl-648">648</a>
<a href="#cl-649">649</a>
<a href="#cl-650">650</a>
<a href="#cl-651">651</a>
<a href="#cl-652">652</a>
<a href="#cl-653">653</a>
<a href="#cl-654">654</a>
<a href="#cl-655">655</a>
<a href="#cl-656">656</a>
<a href="#cl-657">657</a>
<a href="#cl-658">658</a>
<a href="#cl-659">659</a>
<a href="#cl-660">660</a>
<a href="#cl-661">661</a>
<a href="#cl-662">662</a>
<a href="#cl-663">663</a>
<a href="#cl-664">664</a>
<a href="#cl-665">665</a>
<a href="#cl-666">666</a>
<a href="#cl-667">667</a>
<a href="#cl-668">668</a>
<a href="#cl-669">669</a>
<a href="#cl-670">670</a>
<a href="#cl-671">671</a>
<a href="#cl-672">672</a>
<a href="#cl-673">673</a>
<a href="#cl-674">674</a>
<a href="#cl-675">675</a>
<a href="#cl-676">676</a>
<a href="#cl-677">677</a>
<a href="#cl-678">678</a>
<a href="#cl-679">679</a>
<a href="#cl-680">680</a>
<a href="#cl-681">681</a>
<a href="#cl-682">682</a>
<a href="#cl-683">683</a>
<a href="#cl-684">684</a>
<a href="#cl-685">685</a>
<a href="#cl-686">686</a>
<a href="#cl-687">687</a>
<a href="#cl-688">688</a>
<a href="#cl-689">689</a>
<a href="#cl-690">690</a>
<a href="#cl-691">691</a>
<a href="#cl-692">692</a>
<a href="#cl-693">693</a>
<a href="#cl-694">694</a>
<a href="#cl-695">695</a>
<a href="#cl-696">696</a>
<a href="#cl-697">697</a>
<a href="#cl-698">698</a>
<a href="#cl-699">699</a>
<a href="#cl-700">700</a>
<a href="#cl-701">701</a>
<a href="#cl-702">702</a>
<a href="#cl-703">703</a>
<a href="#cl-704">704</a>
<a href="#cl-705">705</a>
<a href="#cl-706">706</a>
<a href="#cl-707">707</a>
<a href="#cl-708">708</a>
<a href="#cl-709">709</a>
<a href="#cl-710">710</a>
<a href="#cl-711">711</a>
<a href="#cl-712">712</a>
<a href="#cl-713">713</a>
<a href="#cl-714">714</a>
<a href="#cl-715">715</a>
<a href="#cl-716">716</a>
<a href="#cl-717">717</a>
<a href="#cl-718">718</a>
<a href="#cl-719">719</a>
<a href="#cl-720">720</a>
<a href="#cl-721">721</a>
<a href="#cl-722">722</a>
<a href="#cl-723">723</a>
<a href="#cl-724">724</a>
<a href="#cl-725">725</a>
<a href="#cl-726">726</a>
<a href="#cl-727">727</a>
<a href="#cl-728">728</a>
<a href="#cl-729">729</a>
<a href="#cl-730">730</a>
<a href="#cl-731">731</a>
<a href="#cl-732">732</a>
<a href="#cl-733">733</a>
<a href="#cl-734">734</a>
<a href="#cl-735">735</a>
<a href="#cl-736">736</a>
<a href="#cl-737">737</a>
<a href="#cl-738">738</a>
<a href="#cl-739">739</a>
<a href="#cl-740">740</a>
<a href="#cl-741">741</a>
<a href="#cl-742">742</a>
<a href="#cl-743">743</a>
<a href="#cl-744">744</a>
<a href="#cl-745">745</a>
<a href="#cl-746">746</a>
<a href="#cl-747">747</a>
<a href="#cl-748">748</a>
<a href="#cl-749">749</a>
<a href="#cl-750">750</a>
<a href="#cl-751">751</a>
<a href="#cl-752">752</a>
<a href="#cl-753">753</a>
<a href="#cl-754">754</a>
<a href="#cl-755">755</a>
<a href="#cl-756">756</a>
<a href="#cl-757">757</a>
<a href="#cl-758">758</a>
<a href="#cl-759">759</a>
<a href="#cl-760">760</a>
<a href="#cl-761">761</a>
<a href="#cl-762">762</a>
<a href="#cl-763">763</a>
<a href="#cl-764">764</a>
<a href="#cl-765">765</a>
<a href="#cl-766">766</a>
<a href="#cl-767">767</a>
<a href="#cl-768">768</a>
<a href="#cl-769">769</a>
<a href="#cl-770">770</a>
<a href="#cl-771">771</a>
<a href="#cl-772">772</a>
<a href="#cl-773">773</a>
<a href="#cl-774">774</a>
<a href="#cl-775">775</a>
<a href="#cl-776">776</a>
<a href="#cl-777">777</a>
<a href="#cl-778">778</a>
<a href="#cl-779">779</a>
<a href="#cl-780">780</a>
<a href="#cl-781">781</a>
<a href="#cl-782">782</a>
<a href="#cl-783">783</a>
<a href="#cl-784">784</a>
<a href="#cl-785">785</a>
<a href="#cl-786">786</a>
<a href="#cl-787">787</a>
<a href="#cl-788">788</a>
<a href="#cl-789">789</a>
<a href="#cl-790">790</a>
<a href="#cl-791">791</a>
<a href="#cl-792">792</a>
<a href="#cl-793">793</a>
<a href="#cl-794">794</a>
<a href="#cl-795">795</a>
<a href="#cl-796">796</a>
<a href="#cl-797">797</a>
<a href="#cl-798">798</a>
<a href="#cl-799">799</a>
<a href="#cl-800">800</a>
<a href="#cl-801">801</a>
<a href="#cl-802">802</a>
<a href="#cl-803">803</a>
<a href="#cl-804">804</a>
<a href="#cl-805">805</a>
<a href="#cl-806">806</a>
<a href="#cl-807">807</a>
<a href="#cl-808">808</a>
<a href="#cl-809">809</a>
<a href="#cl-810">810</a>
<a href="#cl-811">811</a>
<a href="#cl-812">812</a>
<a href="#cl-813">813</a>
<a href="#cl-814">814</a>
<a href="#cl-815">815</a>
<a href="#cl-816">816</a>
<a href="#cl-817">817</a>
<a href="#cl-818">818</a>
<a href="#cl-819">819</a>
<a href="#cl-820">820</a>
<a href="#cl-821">821</a>
<a href="#cl-822">822</a>
<a href="#cl-823">823</a>
<a href="#cl-824">824</a>
<a href="#cl-825">825</a>
<a href="#cl-826">826</a>
<a href="#cl-827">827</a>
<a href="#cl-828">828</a>
<a href="#cl-829">829</a>
<a href="#cl-830">830</a>
<a href="#cl-831">831</a>
<a href="#cl-832">832</a>
<a href="#cl-833">833</a>
<a href="#cl-834">834</a>
<a href="#cl-835">835</a>
<a href="#cl-836">836</a>
<a href="#cl-837">837</a>
<a href="#cl-838">838</a>
<a href="#cl-839">839</a>
<a href="#cl-840">840</a>
<a href="#cl-841">841</a>
<a href="#cl-842">842</a>
<a href="#cl-843">843</a>
<a href="#cl-844">844</a>
<a href="#cl-845">845</a>
<a href="#cl-846">846</a>
<a href="#cl-847">847</a>
<a href="#cl-848">848</a>
<a href="#cl-849">849</a>
<a href="#cl-850">850</a>
<a href="#cl-851">851</a>
<a href="#cl-852">852</a>
<a href="#cl-853">853</a>
<a href="#cl-854">854</a>
<a href="#cl-855">855</a>
<a href="#cl-856">856</a>
<a href="#cl-857">857</a>
<a href="#cl-858">858</a>
<a href="#cl-859">859</a>
<a href="#cl-860">860</a>
<a href="#cl-861">861</a>
<a href="#cl-862">862</a>
<a href="#cl-863">863</a>
<a href="#cl-864">864</a>
<a href="#cl-865">865</a>
<a href="#cl-866">866</a>
<a href="#cl-867">867</a>
<a href="#cl-868">868</a>
<a href="#cl-869">869</a>
<a href="#cl-870">870</a>
<a href="#cl-871">871</a>
<a href="#cl-872">872</a>
<a href="#cl-873">873</a>
<a href="#cl-874">874</a>
<a href="#cl-875">875</a>
<a href="#cl-876">876</a>
<a href="#cl-877">877</a>
<a href="#cl-878">878</a>
<a href="#cl-879">879</a>
<a href="#cl-880">880</a>
<a href="#cl-881">881</a>
<a href="#cl-882">882</a>
<a href="#cl-883">883</a>
<a href="#cl-884">884</a>
<a href="#cl-885">885</a>
<a href="#cl-886">886</a>
<a href="#cl-887">887</a>
<a href="#cl-888">888</a>
<a href="#cl-889">889</a>
<a href="#cl-890">890</a>
<a href="#cl-891">891</a>
<a href="#cl-892">892</a>
<a href="#cl-893">893</a>
<a href="#cl-894">894</a>
<a href="#cl-895">895</a>
<a href="#cl-896">896</a>
<a href="#cl-897">897</a>
<a href="#cl-898">898</a>
<a href="#cl-899">899</a>
<a href="#cl-900">900</a>
<a href="#cl-901">901</a>
<a href="#cl-902">902</a>
<a href="#cl-903">903</a>
<a href="#cl-904">904</a>
<a href="#cl-905">905</a>
<a href="#cl-906">906</a>
<a href="#cl-907">907</a>
<a href="#cl-908">908</a>
<a href="#cl-909">909</a>
<a href="#cl-910">910</a>
<a href="#cl-911">911</a>
<a href="#cl-912">912</a>
<a href="#cl-913">913</a>
<a href="#cl-914">914</a>
<a href="#cl-915">915</a>
<a href="#cl-916">916</a>
<a href="#cl-917">917</a>
<a href="#cl-918">918</a>
<a href="#cl-919">919</a>
<a href="#cl-920">920</a>
<a href="#cl-921">921</a>
<a href="#cl-922">922</a>
<a href="#cl-923">923</a>
<a href="#cl-924">924</a>
<a href="#cl-925">925</a>
<a href="#cl-926">926</a>
<a href="#cl-927">927</a>
<a href="#cl-928">928</a>
<a href="#cl-929">929</a>
<a href="#cl-930">930</a>
<a href="#cl-931">931</a>
<a href="#cl-932">932</a>
<a href="#cl-933">933</a>
<a href="#cl-934">934</a>
<a href="#cl-935">935</a>
<a href="#cl-936">936</a>
<a href="#cl-937">937</a>
<a href="#cl-938">938</a>
<a href="#cl-939">939</a>
<a href="#cl-940">940</a>
<a href="#cl-941">941</a>
<a href="#cl-942">942</a>
<a href="#cl-943">943</a>
<a href="#cl-944">944</a>
<a href="#cl-945">945</a>
<a href="#cl-946">946</a>
<a href="#cl-947">947</a>
<a href="#cl-948">948</a>
<a href="#cl-949">949</a>
<a href="#cl-950">950</a>
<a href="#cl-951">951</a>
<a href="#cl-952">952</a>
<a href="#cl-953">953</a>
<a href="#cl-954">954</a>
<a href="#cl-955">955</a>
<a href="#cl-956">956</a>
<a href="#cl-957">957</a>
<a href="#cl-958">958</a>
<a href="#cl-959">959</a>
<a href="#cl-960">960</a>
<a href="#cl-961">961</a>
<a href="#cl-962">962</a>
<a href="#cl-963">963</a>
<a href="#cl-964">964</a>
<a href="#cl-965">965</a>
<a href="#cl-966">966</a>
<a href="#cl-967">967</a>
<a href="#cl-968">968</a>
<a href="#cl-969">969</a>
<a href="#cl-970">970</a>
<a href="#cl-971">971</a>
<a href="#cl-972">972</a>
<a href="#cl-973">973</a>
<a href="#cl-974">974</a>
<a href="#cl-975">975</a>
<a href="#cl-976">976</a>
<a href="#cl-977">977</a>
<a href="#cl-978">978</a>
<a href="#cl-979">979</a>
<a href="#cl-980">980</a>
<a href="#cl-981">981</a>
<a href="#cl-982">982</a>
<a href="#cl-983">983</a>
<a href="#cl-984">984</a>
<a href="#cl-985">985</a>
<a href="#cl-986">986</a>
<a href="#cl-987">987</a>
<a href="#cl-988">988</a>
<a href="#cl-989">989</a>
<a href="#cl-990">990</a>
<a href="#cl-991">991</a>
<a href="#cl-992">992</a>
<a href="#cl-993">993</a>
<a href="#cl-994">994</a>
<a href="#cl-995">995</a>
<a href="#cl-996">996</a>
<a href="#cl-997">997</a>
<a href="#cl-998">998</a>
<a href="#cl-999">999</a>
<a href="#cl-1000">1000</a>
<a href="#cl-1001">1001</a>
<a href="#cl-1002">1002</a>
<a href="#cl-1003">1003</a>
<a href="#cl-1004">1004</a>
<a href="#cl-1005">1005</a>
<a href="#cl-1006">1006</a>
<a href="#cl-1007">1007</a>
<a href="#cl-1008">1008</a>
<a href="#cl-1009">1009</a>
<a href="#cl-1010">1010</a>
<a href="#cl-1011">1011</a>
<a href="#cl-1012">1012</a>
<a href="#cl-1013">1013</a>
<a href="#cl-1014">1014</a>
<a href="#cl-1015">1015</a>
<a href="#cl-1016">1016</a>
<a href="#cl-1017">1017</a>
<a href="#cl-1018">1018</a>
<a href="#cl-1019">1019</a>
<a href="#cl-1020">1020</a>
<a href="#cl-1021">1021</a>
<a href="#cl-1022">1022</a>
<a href="#cl-1023">1023</a>
<a href="#cl-1024">1024</a>
<a href="#cl-1025">1025</a>
<a href="#cl-1026">1026</a>
<a href="#cl-1027">1027</a>
<a href="#cl-1028">1028</a>
<a href="#cl-1029">1029</a>
<a href="#cl-1030">1030</a>
<a href="#cl-1031">1031</a>
<a href="#cl-1032">1032</a>
<a href="#cl-1033">1033</a>
<a href="#cl-1034">1034</a>
<a href="#cl-1035">1035</a>
<a href="#cl-1036">1036</a>
<a href="#cl-1037">1037</a>
<a href="#cl-1038">1038</a>
<a href="#cl-1039">1039</a>
<a href="#cl-1040">1040</a>
<a href="#cl-1041">1041</a>
<a href="#cl-1042">1042</a>
<a href="#cl-1043">1043</a>
<a href="#cl-1044">1044</a>
<a href="#cl-1045">1045</a>
<a href="#cl-1046">1046</a>
<a href="#cl-1047">1047</a>
<a href="#cl-1048">1048</a>
<a href="#cl-1049">1049</a>
<a href="#cl-1050">1050</a>
<a href="#cl-1051">1051</a>
<a href="#cl-1052">1052</a>
<a href="#cl-1053">1053</a>
<a href="#cl-1054">1054</a>
<a href="#cl-1055">1055</a>
<a href="#cl-1056">1056</a>
<a href="#cl-1057">1057</a>
<a href="#cl-1058">1058</a>
<a href="#cl-1059">1059</a>
<a href="#cl-1060">1060</a>
<a href="#cl-1061">1061</a>
<a href="#cl-1062">1062</a>
<a href="#cl-1063">1063</a>
<a href="#cl-1064">1064</a>
<a href="#cl-1065">1065</a>
<a href="#cl-1066">1066</a>
<a href="#cl-1067">1067</a>
<a href="#cl-1068">1068</a>
<a href="#cl-1069">1069</a>
<a href="#cl-1070">1070</a>
<a href="#cl-1071">1071</a>
<a href="#cl-1072">1072</a>
<a href="#cl-1073">1073</a>
<a href="#cl-1074">1074</a>
<a href="#cl-1075">1075</a>
<a href="#cl-1076">1076</a>
<a href="#cl-1077">1077</a>
<a href="#cl-1078">1078</a>
<a href="#cl-1079">1079</a>
<a href="#cl-1080">1080</a>
<a href="#cl-1081">1081</a>
<a href="#cl-1082">1082</a>
<a href="#cl-1083">1083</a>
<a href="#cl-1084">1084</a>
<a href="#cl-1085">1085</a>
<a href="#cl-1086">1086</a>
<a href="#cl-1087">1087</a>
<a href="#cl-1088">1088</a>
<a href="#cl-1089">1089</a>
<a href="#cl-1090">1090</a>
<a href="#cl-1091">1091</a>
<a href="#cl-1092">1092</a>
<a href="#cl-1093">1093</a>
<a href="#cl-1094">1094</a>
<a href="#cl-1095">1095</a>
<a href="#cl-1096">1096</a>
<a href="#cl-1097">1097</a>
<a href="#cl-1098">1098</a>
<a href="#cl-1099">1099</a>
<a href="#cl-1100">1100</a>
<a href="#cl-1101">1101</a>
<a href="#cl-1102">1102</a>
<a href="#cl-1103">1103</a>
<a href="#cl-1104">1104</a>
<a href="#cl-1105">1105</a>
<a href="#cl-1106">1106</a>
<a href="#cl-1107">1107</a>
<a href="#cl-1108">1108</a>
<a href="#cl-1109">1109</a>
<a href="#cl-1110">1110</a>
<a href="#cl-1111">1111</a>
<a href="#cl-1112">1112</a>
<a href="#cl-1113">1113</a>
<a href="#cl-1114">1114</a>
<a href="#cl-1115">1115</a>
<a href="#cl-1116">1116</a>
<a href="#cl-1117">1117</a>
<a href="#cl-1118">1118</a>
<a href="#cl-1119">1119</a>
<a href="#cl-1120">1120</a>
<a href="#cl-1121">1121</a>
<a href="#cl-1122">1122</a>
<a href="#cl-1123">1123</a>
<a href="#cl-1124">1124</a>
<a href="#cl-1125">1125</a>
<a href="#cl-1126">1126</a>
<a href="#cl-1127">1127</a>
<a href="#cl-1128">1128</a>
<a href="#cl-1129">1129</a>
<a href="#cl-1130">1130</a>
<a href="#cl-1131">1131</a>
<a href="#cl-1132">1132</a>
<a href="#cl-1133">1133</a>
<a href="#cl-1134">1134</a>
<a href="#cl-1135">1135</a>
<a href="#cl-1136">1136</a>
<a href="#cl-1137">1137</a>
<a href="#cl-1138">1138</a>
<a href="#cl-1139">1139</a>
<a href="#cl-1140">1140</a>
<a href="#cl-1141">1141</a>
<a href="#cl-1142">1142</a>
<a href="#cl-1143">1143</a>
<a href="#cl-1144">1144</a>
<a href="#cl-1145">1145</a>
<a href="#cl-1146">1146</a>
<a href="#cl-1147">1147</a>
<a href="#cl-1148">1148</a>
<a href="#cl-1149">1149</a>
<a href="#cl-1150">1150</a>
<a href="#cl-1151">1151</a>
<a href="#cl-1152">1152</a>
<a href="#cl-1153">1153</a>
<a href="#cl-1154">1154</a>
<a href="#cl-1155">1155</a>
<a href="#cl-1156">1156</a>
<a href="#cl-1157">1157</a>
<a href="#cl-1158">1158</a>
<a href="#cl-1159">1159</a>
<a href="#cl-1160">1160</a>
<a href="#cl-1161">1161</a>
<a href="#cl-1162">1162</a>
<a href="#cl-1163">1163</a>
<a href="#cl-1164">1164</a>
<a href="#cl-1165">1165</a>
<a href="#cl-1166">1166</a>
<a href="#cl-1167">1167</a>
<a href="#cl-1168">1168</a>
<a href="#cl-1169">1169</a>
<a href="#cl-1170">1170</a>
<a href="#cl-1171">1171</a>
<a href="#cl-1172">1172</a>
<a href="#cl-1173">1173</a>
<a href="#cl-1174">1174</a>
<a href="#cl-1175">1175</a>
<a href="#cl-1176">1176</a>
<a href="#cl-1177">1177</a>
<a href="#cl-1178">1178</a>
<a href="#cl-1179">1179</a>
<a href="#cl-1180">1180</a>
<a href="#cl-1181">1181</a>
<a href="#cl-1182">1182</a>
<a href="#cl-1183">1183</a>
<a href="#cl-1184">1184</a>
<a href="#cl-1185">1185</a>
<a href="#cl-1186">1186</a>
<a href="#cl-1187">1187</a>
<a href="#cl-1188">1188</a>
<a href="#cl-1189">1189</a>
<a href="#cl-1190">1190</a>
<a href="#cl-1191">1191</a>
<a href="#cl-1192">1192</a>
<a href="#cl-1193">1193</a>
<a href="#cl-1194">1194</a>
<a href="#cl-1195">1195</a>
<a href="#cl-1196">1196</a>
<a href="#cl-1197">1197</a>
<a href="#cl-1198">1198</a>
<a href="#cl-1199">1199</a>
<a href="#cl-1200">1200</a>
<a href="#cl-1201">1201</a>
<a href="#cl-1202">1202</a>
<a href="#cl-1203">1203</a>
<a href="#cl-1204">1204</a>
<a href="#cl-1205">1205</a>
<a href="#cl-1206">1206</a>
<a href="#cl-1207">1207</a>
<a href="#cl-1208">1208</a>
<a href="#cl-1209">1209</a>
<a href="#cl-1210">1210</a>
<a href="#cl-1211">1211</a>
<a href="#cl-1212">1212</a>
<a href="#cl-1213">1213</a>
<a href="#cl-1214">1214</a>
<a href="#cl-1215">1215</a>
<a href="#cl-1216">1216</a>
<a href="#cl-1217">1217</a>
<a href="#cl-1218">1218</a>
<a href="#cl-1219">1219</a>
<a href="#cl-1220">1220</a>
<a href="#cl-1221">1221</a>
<a href="#cl-1222">1222</a>
<a href="#cl-1223">1223</a>
<a href="#cl-1224">1224</a>
<a href="#cl-1225">1225</a>
<a href="#cl-1226">1226</a>
<a href="#cl-1227">1227</a>
<a href="#cl-1228">1228</a>
<a href="#cl-1229">1229</a>
<a href="#cl-1230">1230</a>
<a href="#cl-1231">1231</a>
<a href="#cl-1232">1232</a>
<a href="#cl-1233">1233</a>
<a href="#cl-1234">1234</a>
<a href="#cl-1235">1235</a>
<a href="#cl-1236">1236</a>
<a href="#cl-1237">1237</a>
<a href="#cl-1238">1238</a>
<a href="#cl-1239">1239</a>
<a href="#cl-1240">1240</a>
<a href="#cl-1241">1241</a>
<a href="#cl-1242">1242</a>
<a href="#cl-1243">1243</a>
<a href="#cl-1244">1244</a>
<a href="#cl-1245">1245</a>
<a href="#cl-1246">1246</a>
<a href="#cl-1247">1247</a>
<a href="#cl-1248">1248</a>
<a href="#cl-1249">1249</a>
<a href="#cl-1250">1250</a>
<a href="#cl-1251">1251</a>
<a href="#cl-1252">1252</a>
<a href="#cl-1253">1253</a>
<a href="#cl-1254">1254</a>
<a href="#cl-1255">1255</a>
<a href="#cl-1256">1256</a>
<a href="#cl-1257">1257</a>
<a href="#cl-1258">1258</a>
<a href="#cl-1259">1259</a>
<a href="#cl-1260">1260</a>
<a href="#cl-1261">1261</a>
<a href="#cl-1262">1262</a>
<a href="#cl-1263">1263</a>
<a href="#cl-1264">1264</a>
<a href="#cl-1265">1265</a>
<a href="#cl-1266">1266</a>
<a href="#cl-1267">1267</a>
<a href="#cl-1268">1268</a>
<a href="#cl-1269">1269</a>
<a href="#cl-1270">1270</a>
<a href="#cl-1271">1271</a>
<a href="#cl-1272">1272</a>
<a href="#cl-1273">1273</a>
<a href="#cl-1274">1274</a>
<a href="#cl-1275">1275</a>
<a href="#cl-1276">1276</a>
<a href="#cl-1277">1277</a>
<a href="#cl-1278">1278</a>
<a href="#cl-1279">1279</a>
<a href="#cl-1280">1280</a>
<a href="#cl-1281">1281</a>
<a href="#cl-1282">1282</a>
<a href="#cl-1283">1283</a>
<a href="#cl-1284">1284</a>
<a href="#cl-1285">1285</a>
<a href="#cl-1286">1286</a>
<a href="#cl-1287">1287</a>
<a href="#cl-1288">1288</a>
<a href="#cl-1289">1289</a>
<a href="#cl-1290">1290</a>
<a href="#cl-1291">1291</a>
<a href="#cl-1292">1292</a>
<a href="#cl-1293">1293</a>
<a href="#cl-1294">1294</a>
<a href="#cl-1295">1295</a>
<a href="#cl-1296">1296</a>
<a href="#cl-1297">1297</a>
<a href="#cl-1298">1298</a>
<a href="#cl-1299">1299</a>
<a href="#cl-1300">1300</a>
<a href="#cl-1301">1301</a>
<a href="#cl-1302">1302</a>
<a href="#cl-1303">1303</a>
<a href="#cl-1304">1304</a>
<a href="#cl-1305">1305</a>
<a href="#cl-1306">1306</a>
<a href="#cl-1307">1307</a>
<a href="#cl-1308">1308</a>
<a href="#cl-1309">1309</a>
<a href="#cl-1310">1310</a>
<a href="#cl-1311">1311</a>
<a href="#cl-1312">1312</a>
<a href="#cl-1313">1313</a>
<a href="#cl-1314">1314</a>
<a href="#cl-1315">1315</a>
<a href="#cl-1316">1316</a>
<a href="#cl-1317">1317</a>
<a href="#cl-1318">1318</a>
<a href="#cl-1319">1319</a>
<a href="#cl-1320">1320</a>
<a href="#cl-1321">1321</a>
<a href="#cl-1322">1322</a>
<a href="#cl-1323">1323</a>
<a href="#cl-1324">1324</a>
<a href="#cl-1325">1325</a>
<a href="#cl-1326">1326</a>
<a href="#cl-1327">1327</a>
<a href="#cl-1328">1328</a>
<a href="#cl-1329">1329</a>
<a href="#cl-1330">1330</a>
<a href="#cl-1331">1331</a>
<a href="#cl-1332">1332</a>
<a href="#cl-1333">1333</a>
<a href="#cl-1334">1334</a>
<a href="#cl-1335">1335</a>
<a href="#cl-1336">1336</a>
<a href="#cl-1337">1337</a>
<a href="#cl-1338">1338</a>
<a href="#cl-1339">1339</a>
<a href="#cl-1340">1340</a>
<a href="#cl-1341">1341</a>
<a href="#cl-1342">1342</a>
<a href="#cl-1343">1343</a>
<a href="#cl-1344">1344</a>
<a href="#cl-1345">1345</a>
<a href="#cl-1346">1346</a>
<a href="#cl-1347">1347</a>
<a href="#cl-1348">1348</a>
<a href="#cl-1349">1349</a>
<a href="#cl-1350">1350</a>
<a href="#cl-1351">1351</a>
<a href="#cl-1352">1352</a>
<a href="#cl-1353">1353</a>
<a href="#cl-1354">1354</a>
<a href="#cl-1355">1355</a>
<a href="#cl-1356">1356</a>
<a href="#cl-1357">1357</a>
<a href="#cl-1358">1358</a>
<a href="#cl-1359">1359</a>
<a href="#cl-1360">1360</a>
<a href="#cl-1361">1361</a>
<a href="#cl-1362">1362</a>
<a href="#cl-1363">1363</a>
<a href="#cl-1364">1364</a>
<a href="#cl-1365">1365</a>
<a href="#cl-1366">1366</a>
<a href="#cl-1367">1367</a>
<a href="#cl-1368">1368</a>
<a href="#cl-1369">1369</a>
<a href="#cl-1370">1370</a>
<a href="#cl-1371">1371</a>
<a href="#cl-1372">1372</a>
<a href="#cl-1373">1373</a>
<a href="#cl-1374">1374</a>
<a href="#cl-1375">1375</a>
<a href="#cl-1376">1376</a>
<a href="#cl-1377">1377</a>
<a href="#cl-1378">1378</a>
<a href="#cl-1379">1379</a>
<a href="#cl-1380">1380</a>
<a href="#cl-1381">1381</a>
<a href="#cl-1382">1382</a>
<a href="#cl-1383">1383</a>
<a href="#cl-1384">1384</a>
<a href="#cl-1385">1385</a>
<a href="#cl-1386">1386</a>
<a href="#cl-1387">1387</a>
<a href="#cl-1388">1388</a>
<a href="#cl-1389">1389</a>
<a href="#cl-1390">1390</a>
<a href="#cl-1391">1391</a>
<a href="#cl-1392">1392</a>
<a href="#cl-1393">1393</a>
<a href="#cl-1394">1394</a>
<a href="#cl-1395">1395</a>
<a href="#cl-1396">1396</a>
<a href="#cl-1397">1397</a>
<a href="#cl-1398">1398</a>
<a href="#cl-1399">1399</a>
<a href="#cl-1400">1400</a>
<a href="#cl-1401">1401</a>
<a href="#cl-1402">1402</a>
<a href="#cl-1403">1403</a>
<a href="#cl-1404">1404</a>
<a href="#cl-1405">1405</a>
<a href="#cl-1406">1406</a>
<a href="#cl-1407">1407</a>
<a href="#cl-1408">1408</a>
<a href="#cl-1409">1409</a>
<a href="#cl-1410">1410</a>
<a href="#cl-1411">1411</a>
<a href="#cl-1412">1412</a>
<a href="#cl-1413">1413</a>
<a href="#cl-1414">1414</a>
<a href="#cl-1415">1415</a>
<a href="#cl-1416">1416</a>
<a href="#cl-1417">1417</a>
<a href="#cl-1418">1418</a>
<a href="#cl-1419">1419</a>
<a href="#cl-1420">1420</a>
<a href="#cl-1421">1421</a>
<a href="#cl-1422">1422</a>
<a href="#cl-1423">1423</a>
<a href="#cl-1424">1424</a>
<a href="#cl-1425">1425</a>
<a href="#cl-1426">1426</a>
<a href="#cl-1427">1427</a>
<a href="#cl-1428">1428</a>
<a href="#cl-1429">1429</a>
<a href="#cl-1430">1430</a>
<a href="#cl-1431">1431</a>
<a href="#cl-1432">1432</a>
<a href="#cl-1433">1433</a>
<a href="#cl-1434">1434</a>
<a href="#cl-1435">1435</a>
<a href="#cl-1436">1436</a>
<a href="#cl-1437">1437</a>
<a href="#cl-1438">1438</a>
<a href="#cl-1439">1439</a>
<a href="#cl-1440">1440</a>
<a href="#cl-1441">1441</a>
<a href="#cl-1442">1442</a>
<a href="#cl-1443">1443</a>
<a href="#cl-1444">1444</a>
<a href="#cl-1445">1445</a>
<a href="#cl-1446">1446</a>
<a href="#cl-1447">1447</a>
<a href="#cl-1448">1448</a>
<a href="#cl-1449">1449</a>
<a href="#cl-1450">1450</a>
<a href="#cl-1451">1451</a>
<a href="#cl-1452">1452</a>
<a href="#cl-1453">1453</a>
<a href="#cl-1454">1454</a>
<a href="#cl-1455">1455</a>
<a href="#cl-1456">1456</a>
<a href="#cl-1457">1457</a>
<a href="#cl-1458">1458</a>
<a href="#cl-1459">1459</a>
<a href="#cl-1460">1460</a>
<a href="#cl-1461">1461</a>
<a href="#cl-1462">1462</a>
<a href="#cl-1463">1463</a>
<a href="#cl-1464">1464</a>
<a href="#cl-1465">1465</a>
<a href="#cl-1466">1466</a>
<a href="#cl-1467">1467</a>
<a href="#cl-1468">1468</a>
<a href="#cl-1469">1469</a>
<a href="#cl-1470">1470</a>
<a href="#cl-1471">1471</a>
<a href="#cl-1472">1472</a>
<a href="#cl-1473">1473</a>
<a href="#cl-1474">1474</a>
<a href="#cl-1475">1475</a>
<a href="#cl-1476">1476</a>
<a href="#cl-1477">1477</a>
<a href="#cl-1478">1478</a>
<a href="#cl-1479">1479</a>
<a href="#cl-1480">1480</a>
<a href="#cl-1481">1481</a>
<a href="#cl-1482">1482</a>
<a href="#cl-1483">1483</a>
<a href="#cl-1484">1484</a>
<a href="#cl-1485">1485</a>
<a href="#cl-1486">1486</a>
<a href="#cl-1487">1487</a>
<a href="#cl-1488">1488</a>
<a href="#cl-1489">1489</a>
<a href="#cl-1490">1490</a>
<a href="#cl-1491">1491</a>
<a href="#cl-1492">1492</a>
<a href="#cl-1493">1493</a>
<a href="#cl-1494">1494</a>
<a href="#cl-1495">1495</a>
<a href="#cl-1496">1496</a>
<a href="#cl-1497">1497</a>
<a href="#cl-1498">1498</a>
<a href="#cl-1499">1499</a>
<a href="#cl-1500">1500</a>
<a href="#cl-1501">1501</a>
<a href="#cl-1502">1502</a>
<a href="#cl-1503">1503</a>
<a href="#cl-1504">1504</a>
<a href="#cl-1505">1505</a>
<a href="#cl-1506">1506</a>
<a href="#cl-1507">1507</a>
<a href="#cl-1508">1508</a>
<a href="#cl-1509">1509</a>
<a href="#cl-1510">1510</a>
<a href="#cl-1511">1511</a>
<a href="#cl-1512">1512</a>
<a href="#cl-1513">1513</a>
<a href="#cl-1514">1514</a>
<a href="#cl-1515">1515</a>
<a href="#cl-1516">1516</a>
<a href="#cl-1517">1517</a>
<a href="#cl-1518">1518</a>
<a href="#cl-1519">1519</a>
<a href="#cl-1520">1520</a>
<a href="#cl-1521">1521</a>
<a href="#cl-1522">1522</a>
<a href="#cl-1523">1523</a>
<a href="#cl-1524">1524</a>
<a href="#cl-1525">1525</a>
<a href="#cl-1526">1526</a>
<a href="#cl-1527">1527</a>
<a href="#cl-1528">1528</a>
<a href="#cl-1529">1529</a>
<a href="#cl-1530">1530</a>
<a href="#cl-1531">1531</a>
<a href="#cl-1532">1532</a>
<a href="#cl-1533">1533</a>
<a href="#cl-1534">1534</a>
<a href="#cl-1535">1535</a>
<a href="#cl-1536">1536</a>
<a href="#cl-1537">1537</a>
<a href="#cl-1538">1538</a>
<a href="#cl-1539">1539</a>
<a href="#cl-1540">1540</a>
<a href="#cl-1541">1541</a>
<a href="#cl-1542">1542</a>
<a href="#cl-1543">1543</a>
<a href="#cl-1544">1544</a>
<a href="#cl-1545">1545</a>
<a href="#cl-1546">1546</a>
<a href="#cl-1547">1547</a>
<a href="#cl-1548">1548</a>
<a href="#cl-1549">1549</a>
<a href="#cl-1550">1550</a>
<a href="#cl-1551">1551</a>
<a href="#cl-1552">1552</a>
<a href="#cl-1553">1553</a>
<a href="#cl-1554">1554</a>
<a href="#cl-1555">1555</a>
<a href="#cl-1556">1556</a>
<a href="#cl-1557">1557</a>
<a href="#cl-1558">1558</a>
<a href="#cl-1559">1559</a>
<a href="#cl-1560">1560</a>
<a href="#cl-1561">1561</a>
<a href="#cl-1562">1562</a>
<a href="#cl-1563">1563</a>
<a href="#cl-1564">1564</a>
<a href="#cl-1565">1565</a>
<a href="#cl-1566">1566</a>
<a href="#cl-1567">1567</a>
<a href="#cl-1568">1568</a>
<a href="#cl-1569">1569</a>
<a href="#cl-1570">1570</a>
<a href="#cl-1571">1571</a>
<a href="#cl-1572">1572</a>
<a href="#cl-1573">1573</a>
<a href="#cl-1574">1574</a>
<a href="#cl-1575">1575</a>
<a href="#cl-1576">1576</a>
<a href="#cl-1577">1577</a>
<a href="#cl-1578">1578</a>
<a href="#cl-1579">1579</a>
<a href="#cl-1580">1580</a>
<a href="#cl-1581">1581</a>
<a href="#cl-1582">1582</a>
<a href="#cl-1583">1583</a>
<a href="#cl-1584">1584</a>
<a href="#cl-1585">1585</a>
<a href="#cl-1586">1586</a>
<a href="#cl-1587">1587</a>
<a href="#cl-1588">1588</a>
<a href="#cl-1589">1589</a>
<a href="#cl-1590">1590</a>
<a href="#cl-1591">1591</a>
<a href="#cl-1592">1592</a>
<a href="#cl-1593">1593</a>
<a href="#cl-1594">1594</a>
<a href="#cl-1595">1595</a>
<a href="#cl-1596">1596</a>
<a href="#cl-1597">1597</a>
<a href="#cl-1598">1598</a>
<a href="#cl-1599">1599</a>
<a href="#cl-1600">1600</a>
<a href="#cl-1601">1601</a>
<a href="#cl-1602">1602</a>
<a href="#cl-1603">1603</a>
<a href="#cl-1604">1604</a>
<a href="#cl-1605">1605</a>
<a href="#cl-1606">1606</a>
<a href="#cl-1607">1607</a>
<a href="#cl-1608">1608</a>
<a href="#cl-1609">1609</a>
<a href="#cl-1610">1610</a>
<a href="#cl-1611">1611</a>
<a href="#cl-1612">1612</a>
<a href="#cl-1613">1613</a>
<a href="#cl-1614">1614</a>
<a href="#cl-1615">1615</a>
<a href="#cl-1616">1616</a>
<a href="#cl-1617">1617</a>
<a href="#cl-1618">1618</a>
<a href="#cl-1619">1619</a>
<a href="#cl-1620">1620</a>
<a href="#cl-1621">1621</a>
<a href="#cl-1622">1622</a>
<a href="#cl-1623">1623</a>
<a href="#cl-1624">1624</a>
<a href="#cl-1625">1625</a>
<a href="#cl-1626">1626</a>
<a href="#cl-1627">1627</a>
<a href="#cl-1628">1628</a>
<a href="#cl-1629">1629</a>
<a href="#cl-1630">1630</a>
<a href="#cl-1631">1631</a>
<a href="#cl-1632">1632</a>
<a href="#cl-1633">1633</a>
<a href="#cl-1634">1634</a>
<a href="#cl-1635">1635</a>
<a href="#cl-1636">1636</a>
<a href="#cl-1637">1637</a>
<a href="#cl-1638">1638</a>
<a href="#cl-1639">1639</a>
<a href="#cl-1640">1640</a>
<a href="#cl-1641">1641</a>
<a href="#cl-1642">1642</a>
<a href="#cl-1643">1643</a>
<a href="#cl-1644">1644</a>
<a href="#cl-1645">1645</a>
<a href="#cl-1646">1646</a>
<a href="#cl-1647">1647</a>
<a href="#cl-1648">1648</a>
<a href="#cl-1649">1649</a>
<a href="#cl-1650">1650</a>
<a href="#cl-1651">1651</a>
<a href="#cl-1652">1652</a>
<a href="#cl-1653">1653</a>
<a href="#cl-1654">1654</a>
<a href="#cl-1655">1655</a>
<a href="#cl-1656">1656</a>
<a href="#cl-1657">1657</a>
<a href="#cl-1658">1658</a>
<a href="#cl-1659">1659</a>
<a href="#cl-1660">1660</a>
<a href="#cl-1661">1661</a>
<a href="#cl-1662">1662</a>
<a href="#cl-1663">1663</a>
<a href="#cl-1664">1664</a>
<a href="#cl-1665">1665</a>
<a href="#cl-1666">1666</a>
<a href="#cl-1667">1667</a>
<a href="#cl-1668">1668</a>
<a href="#cl-1669">1669</a>
<a href="#cl-1670">1670</a>
<a href="#cl-1671">1671</a>
<a href="#cl-1672">1672</a>
<a href="#cl-1673">1673</a>
<a href="#cl-1674">1674</a>
<a href="#cl-1675">1675</a>
<a href="#cl-1676">1676</a>
<a href="#cl-1677">1677</a>
<a href="#cl-1678">1678</a>
<a href="#cl-1679">1679</a>
<a href="#cl-1680">1680</a>
<a href="#cl-1681">1681</a>
<a href="#cl-1682">1682</a>
<a href="#cl-1683">1683</a>
<a href="#cl-1684">1684</a>
<a href="#cl-1685">1685</a>
<a href="#cl-1686">1686</a>
<a href="#cl-1687">1687</a>
<a href="#cl-1688">1688</a>
<a href="#cl-1689">1689</a>
<a href="#cl-1690">1690</a>
<a href="#cl-1691">1691</a>
<a href="#cl-1692">1692</a>
<a href="#cl-1693">1693</a>
<a href="#cl-1694">1694</a>
<a href="#cl-1695">1695</a>
<a href="#cl-1696">1696</a>
<a href="#cl-1697">1697</a>
<a href="#cl-1698">1698</a>
<a href="#cl-1699">1699</a>
<a href="#cl-1700">1700</a>
<a href="#cl-1701">1701</a>
<a href="#cl-1702">1702</a>
<a href="#cl-1703">1703</a>
<a href="#cl-1704">1704</a>
<a href="#cl-1705">1705</a>
<a href="#cl-1706">1706</a>
<a href="#cl-1707">1707</a>
<a href="#cl-1708">1708</a>
<a href="#cl-1709">1709</a>
<a href="#cl-1710">1710</a>
<a href="#cl-1711">1711</a>
<a href="#cl-1712">1712</a>
<a href="#cl-1713">1713</a>
<a href="#cl-1714">1714</a>
<a href="#cl-1715">1715</a>
<a href="#cl-1716">1716</a>
<a href="#cl-1717">1717</a>
<a href="#cl-1718">1718</a>
<a href="#cl-1719">1719</a>
<a href="#cl-1720">1720</a>
<a href="#cl-1721">1721</a>
<a href="#cl-1722">1722</a>
<a href="#cl-1723">1723</a>
<a href="#cl-1724">1724</a>
<a href="#cl-1725">1725</a>
<a href="#cl-1726">1726</a>
<a href="#cl-1727">1727</a>
<a href="#cl-1728">1728</a>
<a href="#cl-1729">1729</a>
<a href="#cl-1730">1730</a>
<a href="#cl-1731">1731</a>
<a href="#cl-1732">1732</a>
<a href="#cl-1733">1733</a>
<a href="#cl-1734">1734</a>
<a href="#cl-1735">1735</a>
<a href="#cl-1736">1736</a>
<a href="#cl-1737">1737</a>
<a href="#cl-1738">1738</a>
<a href="#cl-1739">1739</a>
<a href="#cl-1740">1740</a>
<a href="#cl-1741">1741</a>
<a href="#cl-1742">1742</a>
<a href="#cl-1743">1743</a>
<a href="#cl-1744">1744</a>
<a href="#cl-1745">1745</a>
<a href="#cl-1746">1746</a>
<a href="#cl-1747">1747</a>
<a href="#cl-1748">1748</a>
<a href="#cl-1749">1749</a>
<a href="#cl-1750">1750</a>
<a href="#cl-1751">1751</a>
<a href="#cl-1752">1752</a>
<a href="#cl-1753">1753</a>
<a href="#cl-1754">1754</a>
<a href="#cl-1755">1755</a>
<a href="#cl-1756">1756</a>
<a href="#cl-1757">1757</a>
<a href="#cl-1758">1758</a>
<a href="#cl-1759">1759</a>
<a href="#cl-1760">1760</a>
<a href="#cl-1761">1761</a>
<a href="#cl-1762">1762</a>
<a href="#cl-1763">1763</a>
<a href="#cl-1764">1764</a>
<a href="#cl-1765">1765</a>
<a href="#cl-1766">1766</a>
<a href="#cl-1767">1767</a>
<a href="#cl-1768">1768</a>
<a href="#cl-1769">1769</a>
<a href="#cl-1770">1770</a>
<a href="#cl-1771">1771</a>
<a href="#cl-1772">1772</a>
<a href="#cl-1773">1773</a>
<a href="#cl-1774">1774</a>
<a href="#cl-1775">1775</a>
<a href="#cl-1776">1776</a>
<a href="#cl-1777">1777</a>
<a href="#cl-1778">1778</a>
<a href="#cl-1779">1779</a>
<a href="#cl-1780">1780</a>
<a href="#cl-1781">1781</a>
<a href="#cl-1782">1782</a>
<a href="#cl-1783">1783</a>
<a href="#cl-1784">1784</a>
<a href="#cl-1785">1785</a>
<a href="#cl-1786">1786</a>
<a href="#cl-1787">1787</a>
<a href="#cl-1788">1788</a>
<a href="#cl-1789">1789</a>
<a href="#cl-1790">1790</a>
<a href="#cl-1791">1791</a>
<a href="#cl-1792">1792</a>
<a href="#cl-1793">1793</a>
<a href="#cl-1794">1794</a>
<a href="#cl-1795">1795</a>
<a href="#cl-1796">1796</a>
<a href="#cl-1797">1797</a>
<a href="#cl-1798">1798</a>
<a href="#cl-1799">1799</a>
<a href="#cl-1800">1800</a>
<a href="#cl-1801">1801</a>
<a href="#cl-1802">1802</a>
<a href="#cl-1803">1803</a>
<a href="#cl-1804">1804</a>
<a href="#cl-1805">1805</a>
<a href="#cl-1806">1806</a>
<a href="#cl-1807">1807</a>
<a href="#cl-1808">1808</a>
<a href="#cl-1809">1809</a>
<a href="#cl-1810">1810</a>
<a href="#cl-1811">1811</a>
<a href="#cl-1812">1812</a>
<a href="#cl-1813">1813</a>
<a href="#cl-1814">1814</a>
<a href="#cl-1815">1815</a>
<a href="#cl-1816">1816</a>
<a href="#cl-1817">1817</a>
<a href="#cl-1818">1818</a>
<a href="#cl-1819">1819</a>
<a href="#cl-1820">1820</a>
<a href="#cl-1821">1821</a>
<a href="#cl-1822">1822</a>
<a href="#cl-1823">1823</a>
<a href="#cl-1824">1824</a>
<a href="#cl-1825">1825</a>
<a href="#cl-1826">1826</a>
<a href="#cl-1827">1827</a>
<a href="#cl-1828">1828</a>
<a href="#cl-1829">1829</a>
<a href="#cl-1830">1830</a>
<a href="#cl-1831">1831</a>
<a href="#cl-1832">1832</a>
<a href="#cl-1833">1833</a>
<a href="#cl-1834">1834</a>
<a href="#cl-1835">1835</a>
<a href="#cl-1836">1836</a>
<a href="#cl-1837">1837</a>
<a href="#cl-1838">1838</a>
<a href="#cl-1839">1839</a>
<a href="#cl-1840">1840</a>
<a href="#cl-1841">1841</a>
<a href="#cl-1842">1842</a>
<a href="#cl-1843">1843</a>
<a href="#cl-1844">1844</a>
<a href="#cl-1845">1845</a>
<a href="#cl-1846">1846</a>
<a href="#cl-1847">1847</a>
<a href="#cl-1848">1848</a>
<a href="#cl-1849">1849</a>
<a href="#cl-1850">1850</a>
<a href="#cl-1851">1851</a>
<a href="#cl-1852">1852</a>
<a href="#cl-1853">1853</a>
<a href="#cl-1854">1854</a>
<a href="#cl-1855">1855</a>
<a href="#cl-1856">1856</a>
<a href="#cl-1857">1857</a>
<a href="#cl-1858">1858</a>
<a href="#cl-1859">1859</a>
<a href="#cl-1860">1860</a>
<a href="#cl-1861">1861</a>
<a href="#cl-1862">1862</a>
<a href="#cl-1863">1863</a>
<a href="#cl-1864">1864</a>
<a href="#cl-1865">1865</a>
<a href="#cl-1866">1866</a>
<a href="#cl-1867">1867</a>
<a href="#cl-1868">1868</a>
<a href="#cl-1869">1869</a>
<a href="#cl-1870">1870</a>
<a href="#cl-1871">1871</a>
<a href="#cl-1872">1872</a>
<a href="#cl-1873">1873</a>
<a href="#cl-1874">1874</a>
<a href="#cl-1875">1875</a>
<a href="#cl-1876">1876</a>
<a href="#cl-1877">1877</a>
<a href="#cl-1878">1878</a>
<a href="#cl-1879">1879</a>
<a href="#cl-1880">1880</a>
<a href="#cl-1881">1881</a>
<a href="#cl-1882">1882</a>
<a href="#cl-1883">1883</a>
<a href="#cl-1884">1884</a>
<a href="#cl-1885">1885</a>
<a href="#cl-1886">1886</a>
<a href="#cl-1887">1887</a>
<a href="#cl-1888">1888</a>
<a href="#cl-1889">1889</a>
<a href="#cl-1890">1890</a>
<a href="#cl-1891">1891</a>
<a href="#cl-1892">1892</a>
<a href="#cl-1893">1893</a>
<a href="#cl-1894">1894</a>
<a href="#cl-1895">1895</a>
<a href="#cl-1896">1896</a>
<a href="#cl-1897">1897</a>
<a href="#cl-1898">1898</a>
<a href="#cl-1899">1899</a>
<a href="#cl-1900">1900</a>
<a href="#cl-1901">1901</a>
<a href="#cl-1902">1902</a>
<a href="#cl-1903">1903</a>
<a href="#cl-1904">1904</a>
<a href="#cl-1905">1905</a>
<a href="#cl-1906">1906</a>
<a href="#cl-1907">1907</a>
<a href="#cl-1908">1908</a>
<a href="#cl-1909">1909</a>
<a href="#cl-1910">1910</a>
<a href="#cl-1911">1911</a>
<a href="#cl-1912">1912</a>
<a href="#cl-1913">1913</a>
<a href="#cl-1914">1914</a>
<a href="#cl-1915">1915</a>
<a href="#cl-1916">1916</a>
<a href="#cl-1917">1917</a>
<a href="#cl-1918">1918</a>
<a href="#cl-1919">1919</a>
<a href="#cl-1920">1920</a>
<a href="#cl-1921">1921</a>
<a href="#cl-1922">1922</a>
<a href="#cl-1923">1923</a>
<a href="#cl-1924">1924</a>
<a href="#cl-1925">1925</a>
<a href="#cl-1926">1926</a>
<a href="#cl-1927">1927</a>
<a href="#cl-1928">1928</a>
<a href="#cl-1929">1929</a>
<a href="#cl-1930">1930</a>
<a href="#cl-1931">1931</a>
<a href="#cl-1932">1932</a>
<a href="#cl-1933">1933</a>
<a href="#cl-1934">1934</a>
<a href="#cl-1935">1935</a>
<a href="#cl-1936">1936</a>
<a href="#cl-1937">1937</a>
<a href="#cl-1938">1938</a>
<a href="#cl-1939">1939</a>
<a href="#cl-1940">1940</a>
<a href="#cl-1941">1941</a>
<a href="#cl-1942">1942</a>
<a href="#cl-1943">1943</a>
<a href="#cl-1944">1944</a>
<a href="#cl-1945">1945</a>
<a href="#cl-1946">1946</a>
<a href="#cl-1947">1947</a>
<a href="#cl-1948">1948</a>
<a href="#cl-1949">1949</a>
<a href="#cl-1950">1950</a>
<a href="#cl-1951">1951</a>
<a href="#cl-1952">1952</a>
<a href="#cl-1953">1953</a>
<a href="#cl-1954">1954</a>
<a href="#cl-1955">1955</a>
<a href="#cl-1956">1956</a>
<a href="#cl-1957">1957</a>
<a href="#cl-1958">1958</a>
<a href="#cl-1959">1959</a>
<a href="#cl-1960">1960</a>
<a href="#cl-1961">1961</a>
<a href="#cl-1962">1962</a>
<a href="#cl-1963">1963</a>
<a href="#cl-1964">1964</a>
<a href="#cl-1965">1965</a>
<a href="#cl-1966">1966</a>
<a href="#cl-1967">1967</a>
<a href="#cl-1968">1968</a>
<a href="#cl-1969">1969</a>
<a href="#cl-1970">1970</a>
<a href="#cl-1971">1971</a>
<a href="#cl-1972">1972</a>
<a href="#cl-1973">1973</a>
<a href="#cl-1974">1974</a>
<a href="#cl-1975">1975</a>
<a href="#cl-1976">1976</a>
<a href="#cl-1977">1977</a>
<a href="#cl-1978">1978</a>
<a href="#cl-1979">1979</a>
<a href="#cl-1980">1980</a>
<a href="#cl-1981">1981</a>
<a href="#cl-1982">1982</a>
<a href="#cl-1983">1983</a>
<a href="#cl-1984">1984</a>
<a href="#cl-1985">1985</a>
<a href="#cl-1986">1986</a>
<a href="#cl-1987">1987</a>
<a href="#cl-1988">1988</a>
<a href="#cl-1989">1989</a>
<a href="#cl-1990">1990</a>
<a href="#cl-1991">1991</a>
<a href="#cl-1992">1992</a>
<a href="#cl-1993">1993</a>
<a href="#cl-1994">1994</a>
<a href="#cl-1995">1995</a>
<a href="#cl-1996">1996</a>
<a href="#cl-1997">1997</a>
<a href="#cl-1998">1998</a>
<a href="#cl-1999">1999</a>
<a href="#cl-2000">2000</a>
<a href="#cl-2001">2001</a>
<a href="#cl-2002">2002</a>
<a href="#cl-2003">2003</a>
<a href="#cl-2004">2004</a>
<a href="#cl-2005">2005</a>
<a href="#cl-2006">2006</a>
<a href="#cl-2007">2007</a>
<a href="#cl-2008">2008</a>
<a href="#cl-2009">2009</a>
<a href="#cl-2010">2010</a>
<a href="#cl-2011">2011</a>
<a href="#cl-2012">2012</a>
<a href="#cl-2013">2013</a>
<a href="#cl-2014">2014</a>
<a href="#cl-2015">2015</a>
<a href="#cl-2016">2016</a>
<a href="#cl-2017">2017</a>
<a href="#cl-2018">2018</a>
<a href="#cl-2019">2019</a>
<a href="#cl-2020">2020</a>
<a href="#cl-2021">2021</a>
<a href="#cl-2022">2022</a>
<a href="#cl-2023">2023</a>
<a href="#cl-2024">2024</a>
<a href="#cl-2025">2025</a>
<a href="#cl-2026">2026</a>
<a href="#cl-2027">2027</a>
<a href="#cl-2028">2028</a>
<a href="#cl-2029">2029</a>
<a href="#cl-2030">2030</a>
<a href="#cl-2031">2031</a>
<a href="#cl-2032">2032</a>
<a href="#cl-2033">2033</a>
<a href="#cl-2034">2034</a>
<a href="#cl-2035">2035</a>
<a href="#cl-2036">2036</a>
<a href="#cl-2037">2037</a>
<a href="#cl-2038">2038</a>
<a href="#cl-2039">2039</a>
<a href="#cl-2040">2040</a>
<a href="#cl-2041">2041</a>
<a href="#cl-2042">2042</a>
<a href="#cl-2043">2043</a>
<a href="#cl-2044">2044</a>
<a href="#cl-2045">2045</a>
<a href="#cl-2046">2046</a>
<a href="#cl-2047">2047</a>
<a href="#cl-2048">2048</a>
<a href="#cl-2049">2049</a>
<a href="#cl-2050">2050</a>
<a href="#cl-2051">2051</a>
<a href="#cl-2052">2052</a>
<a href="#cl-2053">2053</a>
<a href="#cl-2054">2054</a>
<a href="#cl-2055">2055</a>
<a href="#cl-2056">2056</a>
<a href="#cl-2057">2057</a>
<a href="#cl-2058">2058</a>
<a href="#cl-2059">2059</a>
<a href="#cl-2060">2060</a>
<a href="#cl-2061">2061</a>
<a href="#cl-2062">2062</a>
<a href="#cl-2063">2063</a>
<a href="#cl-2064">2064</a>
<a href="#cl-2065">2065</a>
<a href="#cl-2066">2066</a>
<a href="#cl-2067">2067</a>
<a href="#cl-2068">2068</a>
<a href="#cl-2069">2069</a>
<a href="#cl-2070">2070</a>
<a href="#cl-2071">2071</a>
<a href="#cl-2072">2072</a>
<a href="#cl-2073">2073</a>
<a href="#cl-2074">2074</a>
<a href="#cl-2075">2075</a>
<a href="#cl-2076">2076</a>
<a href="#cl-2077">2077</a>
<a href="#cl-2078">2078</a>
<a href="#cl-2079">2079</a>
<a href="#cl-2080">2080</a>
<a href="#cl-2081">2081</a>
<a href="#cl-2082">2082</a>
<a href="#cl-2083">2083</a>
<a href="#cl-2084">2084</a>
<a href="#cl-2085">2085</a>
<a href="#cl-2086">2086</a>
<a href="#cl-2087">2087</a>
<a href="#cl-2088">2088</a>
<a href="#cl-2089">2089</a>
<a href="#cl-2090">2090</a>
<a href="#cl-2091">2091</a>
<a href="#cl-2092">2092</a>
<a href="#cl-2093">2093</a>
<a href="#cl-2094">2094</a>
<a href="#cl-2095">2095</a>
<a href="#cl-2096">2096</a>
<a href="#cl-2097">2097</a>
<a href="#cl-2098">2098</a>
<a href="#cl-2099">2099</a>
<a href="#cl-2100">2100</a>
<a href="#cl-2101">2101</a>
<a href="#cl-2102">2102</a>
<a href="#cl-2103">2103</a>
<a href="#cl-2104">2104</a>
<a href="#cl-2105">2105</a>
<a href="#cl-2106">2106</a>
<a href="#cl-2107">2107</a>
<a href="#cl-2108">2108</a>
<a href="#cl-2109">2109</a>
<a href="#cl-2110">2110</a>
<a href="#cl-2111">2111</a>
<a href="#cl-2112">2112</a>
<a href="#cl-2113">2113</a>
<a href="#cl-2114">2114</a>
<a href="#cl-2115">2115</a>
<a href="#cl-2116">2116</a>
<a href="#cl-2117">2117</a>
<a href="#cl-2118">2118</a>
<a href="#cl-2119">2119</a>
<a href="#cl-2120">2120</a>
<a href="#cl-2121">2121</a>
<a href="#cl-2122">2122</a>
<a href="#cl-2123">2123</a>
<a href="#cl-2124">2124</a>
<a href="#cl-2125">2125</a>
<a href="#cl-2126">2126</a>
<a href="#cl-2127">2127</a>
<a href="#cl-2128">2128</a>
<a href="#cl-2129">2129</a>
<a href="#cl-2130">2130</a>
<a href="#cl-2131">2131</a>
<a href="#cl-2132">2132</a>
<a href="#cl-2133">2133</a>
<a href="#cl-2134">2134</a>
<a href="#cl-2135">2135</a>
<a href="#cl-2136">2136</a>
<a href="#cl-2137">2137</a>
<a href="#cl-2138">2138</a>
<a href="#cl-2139">2139</a>
<a href="#cl-2140">2140</a>
<a href="#cl-2141">2141</a>
<a href="#cl-2142">2142</a>
<a href="#cl-2143">2143</a>
<a href="#cl-2144">2144</a>
<a href="#cl-2145">2145</a>
<a href="#cl-2146">2146</a>
<a href="#cl-2147">2147</a>
<a href="#cl-2148">2148</a>
<a href="#cl-2149">2149</a>
<a href="#cl-2150">2150</a>
<a href="#cl-2151">2151</a>
<a href="#cl-2152">2152</a>
<a href="#cl-2153">2153</a>
<a href="#cl-2154">2154</a>
<a href="#cl-2155">2155</a>
<a href="#cl-2156">2156</a>
<a href="#cl-2157">2157</a>
<a href="#cl-2158">2158</a>
<a href="#cl-2159">2159</a>
<a href="#cl-2160">2160</a>
<a href="#cl-2161">2161</a>
<a href="#cl-2162">2162</a>
<a href="#cl-2163">2163</a>
<a href="#cl-2164">2164</a>
<a href="#cl-2165">2165</a>
<a href="#cl-2166">2166</a>
<a href="#cl-2167">2167</a>
<a href="#cl-2168">2168</a>
<a href="#cl-2169">2169</a>
<a href="#cl-2170">2170</a>
<a href="#cl-2171">2171</a>
<a href="#cl-2172">2172</a>
<a href="#cl-2173">2173</a>
<a href="#cl-2174">2174</a>
<a href="#cl-2175">2175</a>
<a href="#cl-2176">2176</a>
<a href="#cl-2177">2177</a>
<a href="#cl-2178">2178</a>
<a href="#cl-2179">2179</a>
<a href="#cl-2180">2180</a>
<a href="#cl-2181">2181</a>
<a href="#cl-2182">2182</a>
<a href="#cl-2183">2183</a>
<a href="#cl-2184">2184</a>
<a href="#cl-2185">2185</a>
<a href="#cl-2186">2186</a>
<a href="#cl-2187">2187</a>
<a href="#cl-2188">2188</a>
<a href="#cl-2189">2189</a>
<a href="#cl-2190">2190</a>
<a href="#cl-2191">2191</a>
<a href="#cl-2192">2192</a>
<a href="#cl-2193">2193</a>
<a href="#cl-2194">2194</a>
<a href="#cl-2195">2195</a>
<a href="#cl-2196">2196</a>
<a href="#cl-2197">2197</a>
<a href="#cl-2198">2198</a>
<a href="#cl-2199">2199</a>
<a href="#cl-2200">2200</a>
<a href="#cl-2201">2201</a>
<a href="#cl-2202">2202</a>
<a href="#cl-2203">2203</a>
<a href="#cl-2204">2204</a>
<a href="#cl-2205">2205</a>
<a href="#cl-2206">2206</a>
<a href="#cl-2207">2207</a>
<a href="#cl-2208">2208</a>
<a href="#cl-2209">2209</a>
<a href="#cl-2210">2210</a>
<a href="#cl-2211">2211</a>
<a href="#cl-2212">2212</a>
<a href="#cl-2213">2213</a>
<a href="#cl-2214">2214</a>
<a href="#cl-2215">2215</a>
<a href="#cl-2216">2216</a>
<a href="#cl-2217">2217</a>
<a href="#cl-2218">2218</a>
<a href="#cl-2219">2219</a>
<a href="#cl-2220">2220</a>
<a href="#cl-2221">2221</a>
<a href="#cl-2222">2222</a>
<a href="#cl-2223">2223</a>
<a href="#cl-2224">2224</a>
<a href="#cl-2225">2225</a>
<a href="#cl-2226">2226</a>
<a href="#cl-2227">2227</a>
<a href="#cl-2228">2228</a>
<a href="#cl-2229">2229</a>
<a href="#cl-2230">2230</a>
<a href="#cl-2231">2231</a>
<a href="#cl-2232">2232</a>
<a href="#cl-2233">2233</a>
<a href="#cl-2234">2234</a>
<a href="#cl-2235">2235</a>
<a href="#cl-2236">2236</a>
<a href="#cl-2237">2237</a>
<a href="#cl-2238">2238</a>
<a href="#cl-2239">2239</a>
<a href="#cl-2240">2240</a>
<a href="#cl-2241">2241</a>
<a href="#cl-2242">2242</a>
<a href="#cl-2243">2243</a>
<a href="#cl-2244">2244</a>
<a href="#cl-2245">2245</a>
<a href="#cl-2246">2246</a>
<a href="#cl-2247">2247</a>
<a href="#cl-2248">2248</a>
<a href="#cl-2249">2249</a>
<a href="#cl-2250">2250</a>
<a href="#cl-2251">2251</a>
<a href="#cl-2252">2252</a>
<a href="#cl-2253">2253</a>
<a href="#cl-2254">2254</a>
<a href="#cl-2255">2255</a>
<a href="#cl-2256">2256</a>
<a href="#cl-2257">2257</a>
<a href="#cl-2258">2258</a>
<a href="#cl-2259">2259</a>
<a href="#cl-2260">2260</a>
<a href="#cl-2261">2261</a>
<a href="#cl-2262">2262</a>
<a href="#cl-2263">2263</a>
<a href="#cl-2264">2264</a>
<a href="#cl-2265">2265</a>
<a href="#cl-2266">2266</a>
<a href="#cl-2267">2267</a>
<a href="#cl-2268">2268</a>
<a href="#cl-2269">2269</a>
<a href="#cl-2270">2270</a>
<a href="#cl-2271">2271</a>
<a href="#cl-2272">2272</a>
<a href="#cl-2273">2273</a>
<a href="#cl-2274">2274</a>
<a href="#cl-2275">2275</a>
<a href="#cl-2276">2276</a>
<a href="#cl-2277">2277</a>
<a href="#cl-2278">2278</a>
<a href="#cl-2279">2279</a>
<a href="#cl-2280">2280</a>
<a href="#cl-2281">2281</a>
<a href="#cl-2282">2282</a>
<a href="#cl-2283">2283</a>
<a href="#cl-2284">2284</a>
<a href="#cl-2285">2285</a>
<a href="#cl-2286">2286</a>
<a href="#cl-2287">2287</a>
<a href="#cl-2288">2288</a>
<a href="#cl-2289">2289</a>
<a href="#cl-2290">2290</a>
<a href="#cl-2291">2291</a>
<a href="#cl-2292">2292</a>
<a href="#cl-2293">2293</a>
<a href="#cl-2294">2294</a>
<a href="#cl-2295">2295</a>
<a href="#cl-2296">2296</a>
<a href="#cl-2297">2297</a>
<a href="#cl-2298">2298</a>
<a href="#cl-2299">2299</a>
<a href="#cl-2300">2300</a>
<a href="#cl-2301">2301</a>
<a href="#cl-2302">2302</a>
<a href="#cl-2303">2303</a>
<a href="#cl-2304">2304</a>
<a href="#cl-2305">2305</a>
<a href="#cl-2306">2306</a>
<a href="#cl-2307">2307</a>
<a href="#cl-2308">2308</a>
<a href="#cl-2309">2309</a>
<a href="#cl-2310">2310</a>
<a href="#cl-2311">2311</a>
<a href="#cl-2312">2312</a>
<a href="#cl-2313">2313</a>
<a href="#cl-2314">2314</a>
<a href="#cl-2315">2315</a>
<a href="#cl-2316">2316</a>
<a href="#cl-2317">2317</a>
<a href="#cl-2318">2318</a>
<a href="#cl-2319">2319</a>
<a href="#cl-2320">2320</a>
<a href="#cl-2321">2321</a>
<a href="#cl-2322">2322</a>
<a href="#cl-2323">2323</a>
<a href="#cl-2324">2324</a>
<a href="#cl-2325">2325</a>
<a href="#cl-2326">2326</a>
<a href="#cl-2327">2327</a>
<a href="#cl-2328">2328</a>
<a href="#cl-2329">2329</a>
<a href="#cl-2330">2330</a>
<a href="#cl-2331">2331</a>
<a href="#cl-2332">2332</a>
<a href="#cl-2333">2333</a>
<a href="#cl-2334">2334</a>
<a href="#cl-2335">2335</a>
<a href="#cl-2336">2336</a>
<a href="#cl-2337">2337</a>
<a href="#cl-2338">2338</a>
<a href="#cl-2339">2339</a>
<a href="#cl-2340">2340</a>
<a href="#cl-2341">2341</a>
<a href="#cl-2342">2342</a>
<a href="#cl-2343">2343</a>
<a href="#cl-2344">2344</a>
<a href="#cl-2345">2345</a>
<a href="#cl-2346">2346</a>
<a href="#cl-2347">2347</a>
<a href="#cl-2348">2348</a>
<a href="#cl-2349">2349</a>
<a href="#cl-2350">2350</a>
<a href="#cl-2351">2351</a>
<a href="#cl-2352">2352</a>
<a href="#cl-2353">2353</a>
<a href="#cl-2354">2354</a>
<a href="#cl-2355">2355</a>
<a href="#cl-2356">2356</a>
<a href="#cl-2357">2357</a>
<a href="#cl-2358">2358</a>
<a href="#cl-2359">2359</a>
<a href="#cl-2360">2360</a>
<a href="#cl-2361">2361</a>
<a href="#cl-2362">2362</a>
<a href="#cl-2363">2363</a>
<a href="#cl-2364">2364</a>
<a href="#cl-2365">2365</a>
<a href="#cl-2366">2366</a>
<a href="#cl-2367">2367</a>
<a href="#cl-2368">2368</a>
<a href="#cl-2369">2369</a>
<a href="#cl-2370">2370</a>
<a href="#cl-2371">2371</a>
<a href="#cl-2372">2372</a>
<a href="#cl-2373">2373</a>
<a href="#cl-2374">2374</a>
<a href="#cl-2375">2375</a>
<a href="#cl-2376">2376</a>
<a href="#cl-2377">2377</a>
<a href="#cl-2378">2378</a>
<a href="#cl-2379">2379</a>
<a href="#cl-2380">2380</a>
<a href="#cl-2381">2381</a>
<a href="#cl-2382">2382</a>
<a href="#cl-2383">2383</a>
<a href="#cl-2384">2384</a>
<a href="#cl-2385">2385</a>
<a href="#cl-2386">2386</a>
<a href="#cl-2387">2387</a>
<a href="#cl-2388">2388</a>
<a href="#cl-2389">2389</a>
<a href="#cl-2390">2390</a>
<a href="#cl-2391">2391</a>
<a href="#cl-2392">2392</a>
<a href="#cl-2393">2393</a>
<a href="#cl-2394">2394</a>
<a href="#cl-2395">2395</a>
<a href="#cl-2396">2396</a>
<a href="#cl-2397">2397</a>
<a href="#cl-2398">2398</a>
<a href="#cl-2399">2399</a>
<a href="#cl-2400">2400</a>
<a href="#cl-2401">2401</a>
<a href="#cl-2402">2402</a>
<a href="#cl-2403">2403</a>
<a href="#cl-2404">2404</a>
<a href="#cl-2405">2405</a>
<a href="#cl-2406">2406</a>
<a href="#cl-2407">2407</a>
<a href="#cl-2408">2408</a>
<a href="#cl-2409">2409</a>
<a href="#cl-2410">2410</a>
<a href="#cl-2411">2411</a>
<a href="#cl-2412">2412</a>
<a href="#cl-2413">2413</a>
<a href="#cl-2414">2414</a>
<a href="#cl-2415">2415</a>
<a href="#cl-2416">2416</a>
<a href="#cl-2417">2417</a>
<a href="#cl-2418">2418</a>
<a href="#cl-2419">2419</a>
<a href="#cl-2420">2420</a>
<a href="#cl-2421">2421</a>
<a href="#cl-2422">2422</a>
<a href="#cl-2423">2423</a>
<a href="#cl-2424">2424</a>
<a href="#cl-2425">2425</a>
<a href="#cl-2426">2426</a>
<a href="#cl-2427">2427</a>
<a href="#cl-2428">2428</a>
<a href="#cl-2429">2429</a>
<a href="#cl-2430">2430</a>
<a href="#cl-2431">2431</a>
<a href="#cl-2432">2432</a>
<a href="#cl-2433">2433</a>
<a href="#cl-2434">2434</a>
<a href="#cl-2435">2435</a>
<a href="#cl-2436">2436</a>
<a href="#cl-2437">2437</a>
<a href="#cl-2438">2438</a>
<a href="#cl-2439">2439</a>
<a href="#cl-2440">2440</a>
<a href="#cl-2441">2441</a>
<a href="#cl-2442">2442</a>
<a href="#cl-2443">2443</a>
<a href="#cl-2444">2444</a>
<a href="#cl-2445">2445</a>
<a href="#cl-2446">2446</a>
<a href="#cl-2447">2447</a>
<a href="#cl-2448">2448</a>
<a href="#cl-2449">2449</a>
<a href="#cl-2450">2450</a>
<a href="#cl-2451">2451</a>
<a href="#cl-2452">2452</a>
<a href="#cl-2453">2453</a>
<a href="#cl-2454">2454</a>
<a href="#cl-2455">2455</a>
<a href="#cl-2456">2456</a>
<a href="#cl-2457">2457</a>
<a href="#cl-2458">2458</a>
<a href="#cl-2459">2459</a>
<a href="#cl-2460">2460</a>
<a href="#cl-2461">2461</a>
<a href="#cl-2462">2462</a>
<a href="#cl-2463">2463</a>
<a href="#cl-2464">2464</a>
<a href="#cl-2465">2465</a>
<a href="#cl-2466">2466</a>
<a href="#cl-2467">2467</a>
<a href="#cl-2468">2468</a>
<a href="#cl-2469">2469</a>
<a href="#cl-2470">2470</a>
<a href="#cl-2471">2471</a>
<a href="#cl-2472">2472</a>
<a href="#cl-2473">2473</a>
<a href="#cl-2474">2474</a>
<a href="#cl-2475">2475</a>
<a href="#cl-2476">2476</a>
<a href="#cl-2477">2477</a>
<a href="#cl-2478">2478</a>
<a href="#cl-2479">2479</a>
<a href="#cl-2480">2480</a>
<a href="#cl-2481">2481</a>
<a href="#cl-2482">2482</a>
<a href="#cl-2483">2483</a>
<a href="#cl-2484">2484</a>
<a href="#cl-2485">2485</a>
<a href="#cl-2486">2486</a>
<a href="#cl-2487">2487</a>
<a href="#cl-2488">2488</a>
<a href="#cl-2489">2489</a>
<a href="#cl-2490">2490</a>
<a href="#cl-2491">2491</a>
<a href="#cl-2492">2492</a>
<a href="#cl-2493">2493</a>
<a href="#cl-2494">2494</a>
<a href="#cl-2495">2495</a>
<a href="#cl-2496">2496</a>
<a href="#cl-2497">2497</a>
<a href="#cl-2498">2498</a>
<a href="#cl-2499">2499</a>
<a href="#cl-2500">2500</a>
<a href="#cl-2501">2501</a>
<a href="#cl-2502">2502</a>
<a href="#cl-2503">2503</a>
<a href="#cl-2504">2504</a>
<a href="#cl-2505">2505</a>
<a href="#cl-2506">2506</a>
<a href="#cl-2507">2507</a>
<a href="#cl-2508">2508</a>
<a href="#cl-2509">2509</a>
<a href="#cl-2510">2510</a>
<a href="#cl-2511">2511</a>
<a href="#cl-2512">2512</a>
<a href="#cl-2513">2513</a>
<a href="#cl-2514">2514</a>
<a href="#cl-2515">2515</a>
<a href="#cl-2516">2516</a>
<a href="#cl-2517">2517</a>
<a href="#cl-2518">2518</a>
<a href="#cl-2519">2519</a>
<a href="#cl-2520">2520</a>
<a href="#cl-2521">2521</a>
<a href="#cl-2522">2522</a>
<a href="#cl-2523">2523</a>
<a href="#cl-2524">2524</a>
<a href="#cl-2525">2525</a>
<a href="#cl-2526">2526</a>
<a href="#cl-2527">2527</a>
<a href="#cl-2528">2528</a>
<a href="#cl-2529">2529</a>
<a href="#cl-2530">2530</a>
<a href="#cl-2531">2531</a>
<a href="#cl-2532">2532</a>
<a href="#cl-2533">2533</a>
<a href="#cl-2534">2534</a>
<a href="#cl-2535">2535</a>
<a href="#cl-2536">2536</a>
<a href="#cl-2537">2537</a>
<a href="#cl-2538">2538</a>
<a href="#cl-2539">2539</a>
<a href="#cl-2540">2540</a>
<a href="#cl-2541">2541</a>
<a href="#cl-2542">2542</a>
<a href="#cl-2543">2543</a>
<a href="#cl-2544">2544</a>
<a href="#cl-2545">2545</a>
<a href="#cl-2546">2546</a>
<a href="#cl-2547">2547</a>
<a href="#cl-2548">2548</a>
<a href="#cl-2549">2549</a>
<a href="#cl-2550">2550</a>
<a href="#cl-2551">2551</a>
<a href="#cl-2552">2552</a>
<a href="#cl-2553">2553</a>
<a href="#cl-2554">2554</a>
<a href="#cl-2555">2555</a>
<a href="#cl-2556">2556</a>
<a href="#cl-2557">2557</a>
<a href="#cl-2558">2558</a>
<a href="#cl-2559">2559</a>
<a href="#cl-2560">2560</a>
<a href="#cl-2561">2561</a>
<a href="#cl-2562">2562</a>
<a href="#cl-2563">2563</a>
<a href="#cl-2564">2564</a>
<a href="#cl-2565">2565</a>
<a href="#cl-2566">2566</a>
<a href="#cl-2567">2567</a>
<a href="#cl-2568">2568</a>
<a href="#cl-2569">2569</a>
<a href="#cl-2570">2570</a>
<a href="#cl-2571">2571</a>
<a href="#cl-2572">2572</a>
<a href="#cl-2573">2573</a>
<a href="#cl-2574">2574</a>
<a href="#cl-2575">2575</a>
<a href="#cl-2576">2576</a>
<a href="#cl-2577">2577</a>
<a href="#cl-2578">2578</a>
<a href="#cl-2579">2579</a>
<a href="#cl-2580">2580</a>
<a href="#cl-2581">2581</a>
<a href="#cl-2582">2582</a>
<a href="#cl-2583">2583</a>
<a href="#cl-2584">2584</a>
<a href="#cl-2585">2585</a>
<a href="#cl-2586">2586</a>
<a href="#cl-2587">2587</a>
<a href="#cl-2588">2588</a>
<a href="#cl-2589">2589</a>
<a href="#cl-2590">2590</a>
<a href="#cl-2591">2591</a>
<a href="#cl-2592">2592</a>
<a href="#cl-2593">2593</a>
<a href="#cl-2594">2594</a>
<a href="#cl-2595">2595</a>
<a href="#cl-2596">2596</a>
<a href="#cl-2597">2597</a>
<a href="#cl-2598">2598</a>
<a href="#cl-2599">2599</a>
<a href="#cl-2600">2600</a>
<a href="#cl-2601">2601</a>
<a href="#cl-2602">2602</a>
<a href="#cl-2603">2603</a>
<a href="#cl-2604">2604</a>
<a href="#cl-2605">2605</a>
<a href="#cl-2606">2606</a>
<a href="#cl-2607">2607</a>
<a href="#cl-2608">2608</a>
<a href="#cl-2609">2609</a>
<a href="#cl-2610">2610</a>
<a href="#cl-2611">2611</a>
<a href="#cl-2612">2612</a>
<a href="#cl-2613">2613</a>
<a href="#cl-2614">2614</a>
<a href="#cl-2615">2615</a>
<a href="#cl-2616">2616</a>
<a href="#cl-2617">2617</a>
<a href="#cl-2618">2618</a>
<a href="#cl-2619">2619</a>
<a href="#cl-2620">2620</a>
<a href="#cl-2621">2621</a>
<a href="#cl-2622">2622</a>
<a href="#cl-2623">2623</a>
<a href="#cl-2624">2624</a>
<a href="#cl-2625">2625</a>
<a href="#cl-2626">2626</a>
<a href="#cl-2627">2627</a>
<a href="#cl-2628">2628</a>
<a href="#cl-2629">2629</a>
<a href="#cl-2630">2630</a>
<a href="#cl-2631">2631</a>
<a href="#cl-2632">2632</a>
<a href="#cl-2633">2633</a>
<a href="#cl-2634">2634</a>
<a href="#cl-2635">2635</a>
<a href="#cl-2636">2636</a>
<a href="#cl-2637">2637</a>
<a href="#cl-2638">2638</a>
<a href="#cl-2639">2639</a>
<a href="#cl-2640">2640</a>
<a href="#cl-2641">2641</a>
<a href="#cl-2642">2642</a>
<a href="#cl-2643">2643</a>
<a href="#cl-2644">2644</a>
<a href="#cl-2645">2645</a>
<a href="#cl-2646">2646</a>
<a href="#cl-2647">2647</a>
<a href="#cl-2648">2648</a>
<a href="#cl-2649">2649</a>
<a href="#cl-2650">2650</a>
<a href="#cl-2651">2651</a>
<a href="#cl-2652">2652</a>
<a href="#cl-2653">2653</a>
<a href="#cl-2654">2654</a>
<a href="#cl-2655">2655</a>
<a href="#cl-2656">2656</a>
<a href="#cl-2657">2657</a>
<a href="#cl-2658">2658</a>
<a href="#cl-2659">2659</a>
<a href="#cl-2660">2660</a>
<a href="#cl-2661">2661</a>
<a href="#cl-2662">2662</a>
<a href="#cl-2663">2663</a>
<a href="#cl-2664">2664</a>
<a href="#cl-2665">2665</a>
<a href="#cl-2666">2666</a>
<a href="#cl-2667">2667</a>
<a href="#cl-2668">2668</a>
<a href="#cl-2669">2669</a>
<a href="#cl-2670">2670</a>
<a href="#cl-2671">2671</a>
<a href="#cl-2672">2672</a>
<a href="#cl-2673">2673</a>
<a href="#cl-2674">2674</a>
<a href="#cl-2675">2675</a>
<a href="#cl-2676">2676</a>
<a href="#cl-2677">2677</a>
<a href="#cl-2678">2678</a>
<a href="#cl-2679">2679</a>
<a href="#cl-2680">2680</a>
<a href="#cl-2681">2681</a>
<a href="#cl-2682">2682</a>
<a href="#cl-2683">2683</a>
<a href="#cl-2684">2684</a>
<a href="#cl-2685">2685</a>
<a href="#cl-2686">2686</a>
<a href="#cl-2687">2687</a>
<a href="#cl-2688">2688</a>
<a href="#cl-2689">2689</a>
<a href="#cl-2690">2690</a>
<a href="#cl-2691">2691</a>
<a href="#cl-2692">2692</a>
<a href="#cl-2693">2693</a>
<a href="#cl-2694">2694</a>
<a href="#cl-2695">2695</a>
<a href="#cl-2696">2696</a>
<a href="#cl-2697">2697</a>
<a href="#cl-2698">2698</a>
<a href="#cl-2699">2699</a>
<a href="#cl-2700">2700</a>
<a href="#cl-2701">2701</a>
<a href="#cl-2702">2702</a>
<a href="#cl-2703">2703</a>
<a href="#cl-2704">2704</a>
<a href="#cl-2705">2705</a>
<a href="#cl-2706">2706</a>
<a href="#cl-2707">2707</a>
<a href="#cl-2708">2708</a>
<a href="#cl-2709">2709</a>
<a href="#cl-2710">2710</a>
<a href="#cl-2711">2711</a>
<a href="#cl-2712">2712</a>
<a href="#cl-2713">2713</a>
<a href="#cl-2714">2714</a>
<a href="#cl-2715">2715</a>
<a href="#cl-2716">2716</a>
<a href="#cl-2717">2717</a>
<a href="#cl-2718">2718</a>
<a href="#cl-2719">2719</a>
<a href="#cl-2720">2720</a>
<a href="#cl-2721">2721</a>
<a href="#cl-2722">2722</a>
<a href="#cl-2723">2723</a>
<a href="#cl-2724">2724</a>
<a href="#cl-2725">2725</a>
<a href="#cl-2726">2726</a>
<a href="#cl-2727">2727</a>
<a href="#cl-2728">2728</a>
<a href="#cl-2729">2729</a>
<a href="#cl-2730">2730</a>
<a href="#cl-2731">2731</a>
<a href="#cl-2732">2732</a>
<a href="#cl-2733">2733</a>
<a href="#cl-2734">2734</a>
<a href="#cl-2735">2735</a>
<a href="#cl-2736">2736</a>
<a href="#cl-2737">2737</a>
<a href="#cl-2738">2738</a>
<a href="#cl-2739">2739</a>
<a href="#cl-2740">2740</a>
<a href="#cl-2741">2741</a>
<a href="#cl-2742">2742</a>
<a href="#cl-2743">2743</a>
<a href="#cl-2744">2744</a>
<a href="#cl-2745">2745</a>
<a href="#cl-2746">2746</a>
<a href="#cl-2747">2747</a>
<a href="#cl-2748">2748</a>
<a href="#cl-2749">2749</a>
<a href="#cl-2750">2750</a>
<a href="#cl-2751">2751</a>
<a href="#cl-2752">2752</a>
<a href="#cl-2753">2753</a>
<a href="#cl-2754">2754</a>
<a href="#cl-2755">2755</a>
<a href="#cl-2756">2756</a>
<a href="#cl-2757">2757</a>
<a href="#cl-2758">2758</a>
<a href="#cl-2759">2759</a>
<a href="#cl-2760">2760</a>
<a href="#cl-2761">2761</a>
<a href="#cl-2762">2762</a>
<a href="#cl-2763">2763</a>
<a href="#cl-2764">2764</a>
<a href="#cl-2765">2765</a>
<a href="#cl-2766">2766</a>
<a href="#cl-2767">2767</a>
<a href="#cl-2768">2768</a>
<a href="#cl-2769">2769</a>
<a href="#cl-2770">2770</a>
<a href="#cl-2771">2771</a>
<a href="#cl-2772">2772</a>
<a href="#cl-2773">2773</a>
<a href="#cl-2774">2774</a>
<a href="#cl-2775">2775</a>
<a href="#cl-2776">2776</a>
<a href="#cl-2777">2777</a>
<a href="#cl-2778">2778</a>
<a href="#cl-2779">2779</a>
<a href="#cl-2780">2780</a>
<a href="#cl-2781">2781</a>
<a href="#cl-2782">2782</a>
<a href="#cl-2783">2783</a>
<a href="#cl-2784">2784</a>
<a href="#cl-2785">2785</a>
<a href="#cl-2786">2786</a>
<a href="#cl-2787">2787</a>
<a href="#cl-2788">2788</a>
<a href="#cl-2789">2789</a>
<a href="#cl-2790">2790</a>
<a href="#cl-2791">2791</a>
<a href="#cl-2792">2792</a>
<a href="#cl-2793">2793</a>
<a href="#cl-2794">2794</a>
<a href="#cl-2795">2795</a>
<a href="#cl-2796">2796</a>
<a href="#cl-2797">2797</a>
<a href="#cl-2798">2798</a>
<a href="#cl-2799">2799</a>
<a href="#cl-2800">2800</a>
<a href="#cl-2801">2801</a>
<a href="#cl-2802">2802</a>
<a href="#cl-2803">2803</a>
<a href="#cl-2804">2804</a>
<a href="#cl-2805">2805</a>
<a href="#cl-2806">2806</a>
<a href="#cl-2807">2807</a>
<a href="#cl-2808">2808</a>
<a href="#cl-2809">2809</a>
<a href="#cl-2810">2810</a>
<a href="#cl-2811">2811</a>
<a href="#cl-2812">2812</a>
<a href="#cl-2813">2813</a>
<a href="#cl-2814">2814</a>
<a href="#cl-2815">2815</a>
<a href="#cl-2816">2816</a>
<a href="#cl-2817">2817</a>
<a href="#cl-2818">2818</a>
<a href="#cl-2819">2819</a>
<a href="#cl-2820">2820</a>
<a href="#cl-2821">2821</a>
<a href="#cl-2822">2822</a>
<a href="#cl-2823">2823</a>
<a href="#cl-2824">2824</a>
<a href="#cl-2825">2825</a>
<a href="#cl-2826">2826</a>
<a href="#cl-2827">2827</a>
<a href="#cl-2828">2828</a>
<a href="#cl-2829">2829</a>
<a href="#cl-2830">2830</a>
<a href="#cl-2831">2831</a>
<a href="#cl-2832">2832</a>
<a href="#cl-2833">2833</a>
<a href="#cl-2834">2834</a>
<a href="#cl-2835">2835</a>
<a href="#cl-2836">2836</a>
<a href="#cl-2837">2837</a>
<a href="#cl-2838">2838</a>
<a href="#cl-2839">2839</a>
<a href="#cl-2840">2840</a>
<a href="#cl-2841">2841</a>
<a href="#cl-2842">2842</a>
<a href="#cl-2843">2843</a>
<a href="#cl-2844">2844</a>
<a href="#cl-2845">2845</a>
<a href="#cl-2846">2846</a>
<a href="#cl-2847">2847</a>
<a href="#cl-2848">2848</a>
<a href="#cl-2849">2849</a>
<a href="#cl-2850">2850</a>
<a href="#cl-2851">2851</a>
<a href="#cl-2852">2852</a>
<a href="#cl-2853">2853</a>
<a href="#cl-2854">2854</a>
<a href="#cl-2855">2855</a>
<a href="#cl-2856">2856</a>
<a href="#cl-2857">2857</a>
<a href="#cl-2858">2858</a>
<a href="#cl-2859">2859</a>
<a href="#cl-2860">2860</a>
<a href="#cl-2861">2861</a>
<a href="#cl-2862">2862</a>
<a href="#cl-2863">2863</a>
<a href="#cl-2864">2864</a>
<a href="#cl-2865">2865</a>
<a href="#cl-2866">2866</a>
<a href="#cl-2867">2867</a>
<a href="#cl-2868">2868</a>
<a href="#cl-2869">2869</a>
<a href="#cl-2870">2870</a>
<a href="#cl-2871">2871</a>
<a href="#cl-2872">2872</a>
<a href="#cl-2873">2873</a>
<a href="#cl-2874">2874</a>
<a href="#cl-2875">2875</a>
<a href="#cl-2876">2876</a>
<a href="#cl-2877">2877</a>
<a href="#cl-2878">2878</a>
<a href="#cl-2879">2879</a>
<a href="#cl-2880">2880</a>
<a href="#cl-2881">2881</a>
<a href="#cl-2882">2882</a>
<a href="#cl-2883">2883</a>
<a href="#cl-2884">2884</a>
<a href="#cl-2885">2885</a>
<a href="#cl-2886">2886</a>
<a href="#cl-2887">2887</a>
<a href="#cl-2888">2888</a>
<a href="#cl-2889">2889</a>
<a href="#cl-2890">2890</a>
<a href="#cl-2891">2891</a>
<a href="#cl-2892">2892</a>
<a href="#cl-2893">2893</a>
<a href="#cl-2894">2894</a>
<a href="#cl-2895">2895</a>
<a href="#cl-2896">2896</a>
<a href="#cl-2897">2897</a>
<a href="#cl-2898">2898</a>
<a href="#cl-2899">2899</a>
<a href="#cl-2900">2900</a>
<a href="#cl-2901">2901</a>
<a href="#cl-2902">2902</a>
<a href="#cl-2903">2903</a>
<a href="#cl-2904">2904</a>
<a href="#cl-2905">2905</a>
<a href="#cl-2906">2906</a>
<a href="#cl-2907">2907</a>
<a href="#cl-2908">2908</a>
<a href="#cl-2909">2909</a>
<a href="#cl-2910">2910</a>
<a href="#cl-2911">2911</a>
<a href="#cl-2912">2912</a>
<a href="#cl-2913">2913</a>
<a href="#cl-2914">2914</a>
<a href="#cl-2915">2915</a>
<a href="#cl-2916">2916</a>
<a href="#cl-2917">2917</a>
<a href="#cl-2918">2918</a>
<a href="#cl-2919">2919</a>
<a href="#cl-2920">2920</a>
<a href="#cl-2921">2921</a>
<a href="#cl-2922">2922</a>
<a href="#cl-2923">2923</a>
<a href="#cl-2924">2924</a>
<a href="#cl-2925">2925</a>
<a href="#cl-2926">2926</a>
<a href="#cl-2927">2927</a>
<a href="#cl-2928">2928</a>
<a href="#cl-2929">2929</a>
<a href="#cl-2930">2930</a>
<a href="#cl-2931">2931</a>
<a href="#cl-2932">2932</a>
<a href="#cl-2933">2933</a>
<a href="#cl-2934">2934</a>
<a href="#cl-2935">2935</a>
<a href="#cl-2936">2936</a>
<a href="#cl-2937">2937</a>
<a href="#cl-2938">2938</a>
<a href="#cl-2939">2939</a>
<a href="#cl-2940">2940</a>
<a href="#cl-2941">2941</a>
<a href="#cl-2942">2942</a>
<a href="#cl-2943">2943</a>
<a href="#cl-2944">2944</a>
<a href="#cl-2945">2945</a>
<a href="#cl-2946">2946</a>
<a href="#cl-2947">2947</a>
<a href="#cl-2948">2948</a>
<a href="#cl-2949">2949</a>
<a href="#cl-2950">2950</a>
<a href="#cl-2951">2951</a>
<a href="#cl-2952">2952</a>
<a href="#cl-2953">2953</a>
<a href="#cl-2954">2954</a>
<a href="#cl-2955">2955</a>
<a href="#cl-2956">2956</a>
<a href="#cl-2957">2957</a>
<a href="#cl-2958">2958</a>
<a href="#cl-2959">2959</a>
<a href="#cl-2960">2960</a>
<a href="#cl-2961">2961</a>
<a href="#cl-2962">2962</a>
<a href="#cl-2963">2963</a>
<a href="#cl-2964">2964</a>
<a href="#cl-2965">2965</a>
<a href="#cl-2966">2966</a>
<a href="#cl-2967">2967</a>
<a href="#cl-2968">2968</a>
<a href="#cl-2969">2969</a>
<a href="#cl-2970">2970</a>
<a href="#cl-2971">2971</a>
<a href="#cl-2972">2972</a>
<a href="#cl-2973">2973</a>
<a href="#cl-2974">2974</a>
<a href="#cl-2975">2975</a>
<a href="#cl-2976">2976</a>
<a href="#cl-2977">2977</a>
<a href="#cl-2978">2978</a>
<a href="#cl-2979">2979</a>
<a href="#cl-2980">2980</a>
<a href="#cl-2981">2981</a>
<a href="#cl-2982">2982</a>
<a href="#cl-2983">2983</a>
<a href="#cl-2984">2984</a>
<a href="#cl-2985">2985</a>
<a href="#cl-2986">2986</a>
<a href="#cl-2987">2987</a>
<a href="#cl-2988">2988</a>
<a href="#cl-2989">2989</a>
<a href="#cl-2990">2990</a>
<a href="#cl-2991">2991</a>
<a href="#cl-2992">2992</a>
<a href="#cl-2993">2993</a>
<a href="#cl-2994">2994</a>
<a href="#cl-2995">2995</a>
<a href="#cl-2996">2996</a>
<a href="#cl-2997">2997</a>
<a href="#cl-2998">2998</a>
<a href="#cl-2999">2999</a>
<a href="#cl-3000">3000</a>
<a href="#cl-3001">3001</a>
<a href="#cl-3002">3002</a>
<a href="#cl-3003">3003</a>
<a href="#cl-3004">3004</a>
<a href="#cl-3005">3005</a>
<a href="#cl-3006">3006</a>
<a href="#cl-3007">3007</a>
<a href="#cl-3008">3008</a>
<a href="#cl-3009">3009</a>
<a href="#cl-3010">3010</a>
<a href="#cl-3011">3011</a>
<a href="#cl-3012">3012</a>
<a href="#cl-3013">3013</a>
<a href="#cl-3014">3014</a>
<a href="#cl-3015">3015</a>
<a href="#cl-3016">3016</a>
<a href="#cl-3017">3017</a>
<a href="#cl-3018">3018</a>
<a href="#cl-3019">3019</a>
<a href="#cl-3020">3020</a>
<a href="#cl-3021">3021</a>
<a href="#cl-3022">3022</a>
<a href="#cl-3023">3023</a>
<a href="#cl-3024">3024</a>
<a href="#cl-3025">3025</a>
<a href="#cl-3026">3026</a>
<a href="#cl-3027">3027</a>
<a href="#cl-3028">3028</a>
<a href="#cl-3029">3029</a>
<a href="#cl-3030">3030</a>
<a href="#cl-3031">3031</a>
<a href="#cl-3032">3032</a>
<a href="#cl-3033">3033</a>
<a href="#cl-3034">3034</a>
<a href="#cl-3035">3035</a>
<a href="#cl-3036">3036</a>
<a href="#cl-3037">3037</a>
<a href="#cl-3038">3038</a>
<a href="#cl-3039">3039</a>
<a href="#cl-3040">3040</a>
<a href="#cl-3041">3041</a>
<a href="#cl-3042">3042</a>
<a href="#cl-3043">3043</a>
<a href="#cl-3044">3044</a>
<a href="#cl-3045">3045</a>
<a href="#cl-3046">3046</a>
<a href="#cl-3047">3047</a>
<a href="#cl-3048">3048</a>
<a href="#cl-3049">3049</a>
<a href="#cl-3050">3050</a>
<a href="#cl-3051">3051</a>
<a href="#cl-3052">3052</a>
<a href="#cl-3053">3053</a>
<a href="#cl-3054">3054</a>
<a href="#cl-3055">3055</a>
<a href="#cl-3056">3056</a>
<a href="#cl-3057">3057</a>
<a href="#cl-3058">3058</a>
<a href="#cl-3059">3059</a>
<a href="#cl-3060">3060</a>
<a href="#cl-3061">3061</a>
<a href="#cl-3062">3062</a>
<a href="#cl-3063">3063</a>
<a href="#cl-3064">3064</a>
<a href="#cl-3065">3065</a>
<a href="#cl-3066">3066</a>
<a href="#cl-3067">3067</a>
<a href="#cl-3068">3068</a>
<a href="#cl-3069">3069</a>
<a href="#cl-3070">3070</a>
<a href="#cl-3071">3071</a>
<a href="#cl-3072">3072</a>
<a href="#cl-3073">3073</a>
<a href="#cl-3074">3074</a>
<a href="#cl-3075">3075</a>
<a href="#cl-3076">3076</a>
<a href="#cl-3077">3077</a>
<a href="#cl-3078">3078</a>
<a href="#cl-3079">3079</a>
<a href="#cl-3080">3080</a>
<a href="#cl-3081">3081</a>
<a href="#cl-3082">3082</a>
<a href="#cl-3083">3083</a>
<a href="#cl-3084">3084</a>
<a href="#cl-3085">3085</a>
<a href="#cl-3086">3086</a>
<a href="#cl-3087">3087</a>
<a href="#cl-3088">3088</a>
<a href="#cl-3089">3089</a>
<a href="#cl-3090">3090</a>
<a href="#cl-3091">3091</a>
<a href="#cl-3092">3092</a>
<a href="#cl-3093">3093</a>
<a href="#cl-3094">3094</a>
<a href="#cl-3095">3095</a>
<a href="#cl-3096">3096</a>
<a href="#cl-3097">3097</a>
<a href="#cl-3098">3098</a>
<a href="#cl-3099">3099</a>
<a href="#cl-3100">3100</a>
<a href="#cl-3101">3101</a>
<a href="#cl-3102">3102</a>
<a href="#cl-3103">3103</a>
<a href="#cl-3104">3104</a>
<a href="#cl-3105">3105</a>
<a href="#cl-3106">3106</a>
<a href="#cl-3107">3107</a>
<a href="#cl-3108">3108</a>
<a href="#cl-3109">3109</a>
<a href="#cl-3110">3110</a>
<a href="#cl-3111">3111</a>
<a href="#cl-3112">3112</a>
<a href="#cl-3113">3113</a>
<a href="#cl-3114">3114</a>
<a href="#cl-3115">3115</a>
<a href="#cl-3116">3116</a>
<a href="#cl-3117">3117</a>
<a href="#cl-3118">3118</a>
<a href="#cl-3119">3119</a>
<a href="#cl-3120">3120</a>
<a href="#cl-3121">3121</a>
<a href="#cl-3122">3122</a>
<a href="#cl-3123">3123</a>
<a href="#cl-3124">3124</a>
<a href="#cl-3125">3125</a>
<a href="#cl-3126">3126</a>
<a href="#cl-3127">3127</a>
<a href="#cl-3128">3128</a>
<a href="#cl-3129">3129</a>
<a href="#cl-3130">3130</a>
<a href="#cl-3131">3131</a>
<a href="#cl-3132">3132</a>
<a href="#cl-3133">3133</a>
<a href="#cl-3134">3134</a>
<a href="#cl-3135">3135</a>
<a href="#cl-3136">3136</a>
<a href="#cl-3137">3137</a>
<a href="#cl-3138">3138</a>
<a href="#cl-3139">3139</a>
<a href="#cl-3140">3140</a>
<a href="#cl-3141">3141</a>
<a href="#cl-3142">3142</a>
<a href="#cl-3143">3143</a>
<a href="#cl-3144">3144</a>
<a href="#cl-3145">3145</a>
<a href="#cl-3146">3146</a>
<a href="#cl-3147">3147</a>
<a href="#cl-3148">3148</a>
<a href="#cl-3149">3149</a>
<a href="#cl-3150">3150</a>
<a href="#cl-3151">3151</a>
<a href="#cl-3152">3152</a>
<a href="#cl-3153">3153</a>
<a href="#cl-3154">3154</a>
<a href="#cl-3155">3155</a>
<a href="#cl-3156">3156</a>
<a href="#cl-3157">3157</a>
<a href="#cl-3158">3158</a>
<a href="#cl-3159">3159</a>
<a href="#cl-3160">3160</a>
<a href="#cl-3161">3161</a>
<a href="#cl-3162">3162</a>
<a href="#cl-3163">3163</a>
<a href="#cl-3164">3164</a>
<a href="#cl-3165">3165</a>
<a href="#cl-3166">3166</a>
<a href="#cl-3167">3167</a>
<a href="#cl-3168">3168</a>
<a href="#cl-3169">3169</a>
<a href="#cl-3170">3170</a>
<a href="#cl-3171">3171</a>
<a href="#cl-3172">3172</a>
<a href="#cl-3173">3173</a>
<a href="#cl-3174">3174</a>
<a href="#cl-3175">3175</a>
<a href="#cl-3176">3176</a>
<a href="#cl-3177">3177</a>
<a href="#cl-3178">3178</a>
<a href="#cl-3179">3179</a>
<a href="#cl-3180">3180</a>
<a href="#cl-3181">3181</a>
<a href="#cl-3182">3182</a>
<a href="#cl-3183">3183</a>
<a href="#cl-3184">3184</a>
<a href="#cl-3185">3185</a>
<a href="#cl-3186">3186</a>
<a href="#cl-3187">3187</a>
<a href="#cl-3188">3188</a>
<a href="#cl-3189">3189</a>
<a href="#cl-3190">3190</a>
<a href="#cl-3191">3191</a>
<a href="#cl-3192">3192</a>
<a href="#cl-3193">3193</a>
<a href="#cl-3194">3194</a>
<a href="#cl-3195">3195</a>
<a href="#cl-3196">3196</a>
<a href="#cl-3197">3197</a>
<a href="#cl-3198">3198</a>
<a href="#cl-3199">3199</a>
<a href="#cl-3200">3200</a>
<a href="#cl-3201">3201</a>
<a href="#cl-3202">3202</a>
<a href="#cl-3203">3203</a>
<a href="#cl-3204">3204</a>
<a href="#cl-3205">3205</a>
<a href="#cl-3206">3206</a>
<a href="#cl-3207">3207</a>
<a href="#cl-3208">3208</a>
<a href="#cl-3209">3209</a>
<a href="#cl-3210">3210</a>
<a href="#cl-3211">3211</a>
<a href="#cl-3212">3212</a>
<a href="#cl-3213">3213</a>
<a href="#cl-3214">3214</a>
<a href="#cl-3215">3215</a>
<a href="#cl-3216">3216</a>
<a href="#cl-3217">3217</a>
<a href="#cl-3218">3218</a>
<a href="#cl-3219">3219</a>
<a href="#cl-3220">3220</a>
<a href="#cl-3221">3221</a>
<a href="#cl-3222">3222</a>
<a href="#cl-3223">3223</a>
<a href="#cl-3224">3224</a>
<a href="#cl-3225">3225</a>
<a href="#cl-3226">3226</a>
<a href="#cl-3227">3227</a>
<a href="#cl-3228">3228</a>
<a href="#cl-3229">3229</a>
<a href="#cl-3230">3230</a>
<a href="#cl-3231">3231</a>
<a href="#cl-3232">3232</a>
<a href="#cl-3233">3233</a>
<a href="#cl-3234">3234</a>
<a href="#cl-3235">3235</a>
<a href="#cl-3236">3236</a>
<a href="#cl-3237">3237</a>
<a href="#cl-3238">3238</a>
<a href="#cl-3239">3239</a>
<a href="#cl-3240">3240</a>
<a href="#cl-3241">3241</a>
<a href="#cl-3242">3242</a>
<a href="#cl-3243">3243</a>
<a href="#cl-3244">3244</a>
<a href="#cl-3245">3245</a>
<a href="#cl-3246">3246</a>
<a href="#cl-3247">3247</a>
<a href="#cl-3248">3248</a>
<a href="#cl-3249">3249</a>
<a href="#cl-3250">3250</a>
<a href="#cl-3251">3251</a>
<a href="#cl-3252">3252</a>
<a href="#cl-3253">3253</a>
<a href="#cl-3254">3254</a>
<a href="#cl-3255">3255</a>
<a href="#cl-3256">3256</a>
<a href="#cl-3257">3257</a>
<a href="#cl-3258">3258</a>
<a href="#cl-3259">3259</a>
<a href="#cl-3260">3260</a>
<a href="#cl-3261">3261</a>
<a href="#cl-3262">3262</a>
<a href="#cl-3263">3263</a>
<a href="#cl-3264">3264</a>
<a href="#cl-3265">3265</a>
<a href="#cl-3266">3266</a>
<a href="#cl-3267">3267</a>
<a href="#cl-3268">3268</a>
<a href="#cl-3269">3269</a>
<a href="#cl-3270">3270</a>
<a href="#cl-3271">3271</a>
<a href="#cl-3272">3272</a>
<a href="#cl-3273">3273</a>
<a href="#cl-3274">3274</a>
<a href="#cl-3275">3275</a>
<a href="#cl-3276">3276</a>
<a href="#cl-3277">3277</a>
<a href="#cl-3278">3278</a>
<a href="#cl-3279">3279</a>
<a href="#cl-3280">3280</a>
<a href="#cl-3281">3281</a>
<a href="#cl-3282">3282</a>
<a href="#cl-3283">3283</a>
<a href="#cl-3284">3284</a>
<a href="#cl-3285">3285</a>
<a href="#cl-3286">3286</a>
<a href="#cl-3287">3287</a>
<a href="#cl-3288">3288</a>
<a href="#cl-3289">3289</a>
<a href="#cl-3290">3290</a>
<a href="#cl-3291">3291</a>
<a href="#cl-3292">3292</a>
<a href="#cl-3293">3293</a>
<a href="#cl-3294">3294</a>
<a href="#cl-3295">3295</a>
<a href="#cl-3296">3296</a>
<a href="#cl-3297">3297</a>
<a href="#cl-3298">3298</a>
<a href="#cl-3299">3299</a>
<a href="#cl-3300">3300</a>
<a href="#cl-3301">3301</a>
<a href="#cl-3302">3302</a>
<a href="#cl-3303">3303</a>
<a href="#cl-3304">3304</a>
<a href="#cl-3305">3305</a>
<a href="#cl-3306">3306</a>
<a href="#cl-3307">3307</a>
<a href="#cl-3308">3308</a>
<a href="#cl-3309">3309</a>
<a href="#cl-3310">3310</a>
<a href="#cl-3311">3311</a>
<a href="#cl-3312">3312</a>
<a href="#cl-3313">3313</a>
<a href="#cl-3314">3314</a>
<a href="#cl-3315">3315</a>
<a href="#cl-3316">3316</a>
<a href="#cl-3317">3317</a>
<a href="#cl-3318">3318</a>
<a href="#cl-3319">3319</a>
<a href="#cl-3320">3320</a>
<a href="#cl-3321">3321</a>
<a href="#cl-3322">3322</a>
<a href="#cl-3323">3323</a>
<a href="#cl-3324">3324</a>
<a href="#cl-3325">3325</a>
<a href="#cl-3326">3326</a>
<a href="#cl-3327">3327</a>
<a href="#cl-3328">3328</a>
<a href="#cl-3329">3329</a>
<a href="#cl-3330">3330</a>
<a href="#cl-3331">3331</a>
<a href="#cl-3332">3332</a>
<a href="#cl-3333">3333</a>
<a href="#cl-3334">3334</a>
<a href="#cl-3335">3335</a>
<a href="#cl-3336">3336</a>
<a href="#cl-3337">3337</a>
<a href="#cl-3338">3338</a>
<a href="#cl-3339">3339</a>
<a href="#cl-3340">3340</a>
<a href="#cl-3341">3341</a>
<a href="#cl-3342">3342</a>
<a href="#cl-3343">3343</a>
<a href="#cl-3344">3344</a>
<a href="#cl-3345">3345</a>
<a href="#cl-3346">3346</a>
<a href="#cl-3347">3347</a>
<a href="#cl-3348">3348</a>
<a href="#cl-3349">3349</a>
<a href="#cl-3350">3350</a>
<a href="#cl-3351">3351</a>
<a href="#cl-3352">3352</a>
<a href="#cl-3353">3353</a>
<a href="#cl-3354">3354</a>
<a href="#cl-3355">3355</a>
<a href="#cl-3356">3356</a>
<a href="#cl-3357">3357</a>
<a href="#cl-3358">3358</a>
<a href="#cl-3359">3359</a>
<a href="#cl-3360">3360</a>
<a href="#cl-3361">3361</a>
<a href="#cl-3362">3362</a>
<a href="#cl-3363">3363</a>
<a href="#cl-3364">3364</a>
<a href="#cl-3365">3365</a>
<a href="#cl-3366">3366</a>
<a href="#cl-3367">3367</a>
<a href="#cl-3368">3368</a>
<a href="#cl-3369">3369</a>
<a href="#cl-3370">3370</a>
<a href="#cl-3371">3371</a>
<a href="#cl-3372">3372</a>
<a href="#cl-3373">3373</a>
<a href="#cl-3374">3374</a>
<a href="#cl-3375">3375</a>
<a href="#cl-3376">3376</a>
<a href="#cl-3377">3377</a>
<a href="#cl-3378">3378</a>
<a href="#cl-3379">3379</a>
<a href="#cl-3380">3380</a>
<a href="#cl-3381">3381</a>
<a href="#cl-3382">3382</a>
<a href="#cl-3383">3383</a>
<a href="#cl-3384">3384</a>
<a href="#cl-3385">3385</a>
<a href="#cl-3386">3386</a>
<a href="#cl-3387">3387</a>
<a href="#cl-3388">3388</a>
<a href="#cl-3389">3389</a>
<a href="#cl-3390">3390</a>
<a href="#cl-3391">3391</a>
<a href="#cl-3392">3392</a>
<a href="#cl-3393">3393</a>
<a href="#cl-3394">3394</a>
<a href="#cl-3395">3395</a>
<a href="#cl-3396">3396</a>
<a href="#cl-3397">3397</a>
<a href="#cl-3398">3398</a>
<a href="#cl-3399">3399</a>
<a href="#cl-3400">3400</a>
<a href="#cl-3401">3401</a>
<a href="#cl-3402">3402</a>
<a href="#cl-3403">3403</a>
<a href="#cl-3404">3404</a>
<a href="#cl-3405">3405</a>
<a href="#cl-3406">3406</a>
<a href="#cl-3407">3407</a>
<a href="#cl-3408">3408</a>
<a href="#cl-3409">3409</a>
<a href="#cl-3410">3410</a>
<a href="#cl-3411">3411</a>
<a href="#cl-3412">3412</a>
<a href="#cl-3413">3413</a>
<a href="#cl-3414">3414</a>
<a href="#cl-3415">3415</a>
<a href="#cl-3416">3416</a>
<a href="#cl-3417">3417</a>
<a href="#cl-3418">3418</a>
<a href="#cl-3419">3419</a>
<a href="#cl-3420">3420</a>
<a href="#cl-3421">3421</a>
<a href="#cl-3422">3422</a>
<a href="#cl-3423">3423</a>
<a href="#cl-3424">3424</a>
<a href="#cl-3425">3425</a>
<a href="#cl-3426">3426</a>
<a href="#cl-3427">3427</a>
<a href="#cl-3428">3428</a>
<a href="#cl-3429">3429</a>
<a href="#cl-3430">3430</a>
<a href="#cl-3431">3431</a>
<a href="#cl-3432">3432</a>
<a href="#cl-3433">3433</a>
<a href="#cl-3434">3434</a>
<a href="#cl-3435">3435</a>
<a href="#cl-3436">3436</a>
<a href="#cl-3437">3437</a>
<a href="#cl-3438">3438</a>
<a href="#cl-3439">3439</a>
<a href="#cl-3440">3440</a>
<a href="#cl-3441">3441</a>
<a href="#cl-3442">3442</a>
<a href="#cl-3443">3443</a>
<a href="#cl-3444">3444</a>
<a href="#cl-3445">3445</a>
<a href="#cl-3446">3446</a>
<a href="#cl-3447">3447</a>
<a href="#cl-3448">3448</a>
<a href="#cl-3449">3449</a>
<a href="#cl-3450">3450</a>
<a href="#cl-3451">3451</a>
<a href="#cl-3452">3452</a>
<a href="#cl-3453">3453</a>
<a href="#cl-3454">3454</a>
<a href="#cl-3455">3455</a>
<a href="#cl-3456">3456</a>
<a href="#cl-3457">3457</a>
<a href="#cl-3458">3458</a>
<a href="#cl-3459">3459</a>
<a href="#cl-3460">3460</a>
<a href="#cl-3461">3461</a>
<a href="#cl-3462">3462</a>
<a href="#cl-3463">3463</a>
<a href="#cl-3464">3464</a>
<a href="#cl-3465">3465</a>
<a href="#cl-3466">3466</a>
<a href="#cl-3467">3467</a>
<a href="#cl-3468">3468</a>
<a href="#cl-3469">3469</a>
<a href="#cl-3470">3470</a>
<a href="#cl-3471">3471</a>
<a href="#cl-3472">3472</a>
<a href="#cl-3473">3473</a>
<a href="#cl-3474">3474</a>
<a href="#cl-3475">3475</a>
<a href="#cl-3476">3476</a>
<a href="#cl-3477">3477</a>
<a href="#cl-3478">3478</a>
<a href="#cl-3479">3479</a>
<a href="#cl-3480">3480</a>
<a href="#cl-3481">3481</a>
<a href="#cl-3482">3482</a>
<a href="#cl-3483">3483</a>
<a href="#cl-3484">3484</a>
<a href="#cl-3485">3485</a>
<a href="#cl-3486">3486</a>
<a href="#cl-3487">3487</a>
<a href="#cl-3488">3488</a>
<a href="#cl-3489">3489</a>
<a href="#cl-3490">3490</a>
<a href="#cl-3491">3491</a>
<a href="#cl-3492">3492</a>
<a href="#cl-3493">3493</a>
<a href="#cl-3494">3494</a>
<a href="#cl-3495">3495</a>
<a href="#cl-3496">3496</a>
<a href="#cl-3497">3497</a>
<a href="#cl-3498">3498</a>
<a href="#cl-3499">3499</a>
<a href="#cl-3500">3500</a>
<a href="#cl-3501">3501</a>
<a href="#cl-3502">3502</a>
<a href="#cl-3503">3503</a>
<a href="#cl-3504">3504</a>
<a href="#cl-3505">3505</a>
<a href="#cl-3506">3506</a>
<a href="#cl-3507">3507</a>
<a href="#cl-3508">3508</a>
<a href="#cl-3509">3509</a>
<a href="#cl-3510">3510</a>
<a href="#cl-3511">3511</a>
<a href="#cl-3512">3512</a>
<a href="#cl-3513">3513</a>
<a href="#cl-3514">3514</a>
<a href="#cl-3515">3515</a>
<a href="#cl-3516">3516</a>
<a href="#cl-3517">3517</a>
<a href="#cl-3518">3518</a>
<a href="#cl-3519">3519</a>
<a href="#cl-3520">3520</a>
<a href="#cl-3521">3521</a>
<a href="#cl-3522">3522</a>
<a href="#cl-3523">3523</a>
<a href="#cl-3524">3524</a>
<a href="#cl-3525">3525</a>
<a href="#cl-3526">3526</a>
<a href="#cl-3527">3527</a>
<a href="#cl-3528">3528</a>
<a href="#cl-3529">3529</a>
<a href="#cl-3530">3530</a>
<a href="#cl-3531">3531</a>
<a href="#cl-3532">3532</a>
<a href="#cl-3533">3533</a>
<a href="#cl-3534">3534</a>
<a href="#cl-3535">3535</a>
<a href="#cl-3536">3536</a>
<a href="#cl-3537">3537</a>
<a href="#cl-3538">3538</a>
<a href="#cl-3539">3539</a>
<a href="#cl-3540">3540</a>
<a href="#cl-3541">3541</a>
<a href="#cl-3542">3542</a>
<a href="#cl-3543">3543</a>
<a href="#cl-3544">3544</a>
<a href="#cl-3545">3545</a>
<a href="#cl-3546">3546</a>
<a href="#cl-3547">3547</a>
<a href="#cl-3548">3548</a>
<a href="#cl-3549">3549</a>
<a href="#cl-3550">3550</a>
<a href="#cl-3551">3551</a>
<a href="#cl-3552">3552</a>
<a href="#cl-3553">3553</a>
<a href="#cl-3554">3554</a>
<a href="#cl-3555">3555</a>
<a href="#cl-3556">3556</a>
<a href="#cl-3557">3557</a>
<a href="#cl-3558">3558</a>
<a href="#cl-3559">3559</a>
<a href="#cl-3560">3560</a>
<a href="#cl-3561">3561</a>
<a href="#cl-3562">3562</a>
<a href="#cl-3563">3563</a>
<a href="#cl-3564">3564</a>
<a href="#cl-3565">3565</a>
<a href="#cl-3566">3566</a>
<a href="#cl-3567">3567</a>
<a href="#cl-3568">3568</a>
<a href="#cl-3569">3569</a>
<a href="#cl-3570">3570</a>
<a href="#cl-3571">3571</a>
<a href="#cl-3572">3572</a>
<a href="#cl-3573">3573</a>
<a href="#cl-3574">3574</a>
<a href="#cl-3575">3575</a>
<a href="#cl-3576">3576</a>
<a href="#cl-3577">3577</a>
<a href="#cl-3578">3578</a>
<a href="#cl-3579">3579</a>
<a href="#cl-3580">3580</a>
<a href="#cl-3581">3581</a>
<a href="#cl-3582">3582</a>
<a href="#cl-3583">3583</a>
<a href="#cl-3584">3584</a>
<a href="#cl-3585">3585</a>
<a href="#cl-3586">3586</a>
<a href="#cl-3587">3587</a>
<a href="#cl-3588">3588</a>
<a href="#cl-3589">3589</a>
<a href="#cl-3590">3590</a>
<a href="#cl-3591">3591</a>
<a href="#cl-3592">3592</a>
<a href="#cl-3593">3593</a>
<a href="#cl-3594">3594</a>
<a href="#cl-3595">3595</a>
<a href="#cl-3596">3596</a>
<a href="#cl-3597">3597</a>
<a href="#cl-3598">3598</a>
<a href="#cl-3599">3599</a>
<a href="#cl-3600">3600</a>
<a href="#cl-3601">3601</a>
<a href="#cl-3602">3602</a>
<a href="#cl-3603">3603</a>
<a href="#cl-3604">3604</a>
<a href="#cl-3605">3605</a>
<a href="#cl-3606">3606</a>
<a href="#cl-3607">3607</a>
<a href="#cl-3608">3608</a>
<a href="#cl-3609">3609</a>
<a href="#cl-3610">3610</a>
<a href="#cl-3611">3611</a>
<a href="#cl-3612">3612</a>
<a href="#cl-3613">3613</a>
<a href="#cl-3614">3614</a>
<a href="#cl-3615">3615</a>
<a href="#cl-3616">3616</a>
<a href="#cl-3617">3617</a>
<a href="#cl-3618">3618</a>
<a href="#cl-3619">3619</a>
<a href="#cl-3620">3620</a>
<a href="#cl-3621">3621</a>
<a href="#cl-3622">3622</a>
<a href="#cl-3623">3623</a>
<a href="#cl-3624">3624</a>
<a href="#cl-3625">3625</a>
<a href="#cl-3626">3626</a>
<a href="#cl-3627">3627</a>
<a href="#cl-3628">3628</a>
<a href="#cl-3629">3629</a>
<a href="#cl-3630">3630</a>
<a href="#cl-3631">3631</a>
<a href="#cl-3632">3632</a>
<a href="#cl-3633">3633</a>
<a href="#cl-3634">3634</a>
<a href="#cl-3635">3635</a>
<a href="#cl-3636">3636</a>
<a href="#cl-3637">3637</a>
<a href="#cl-3638">3638</a>
<a href="#cl-3639">3639</a>
<a href="#cl-3640">3640</a>
<a href="#cl-3641">3641</a>
<a href="#cl-3642">3642</a>
<a href="#cl-3643">3643</a>
<a href="#cl-3644">3644</a>
<a href="#cl-3645">3645</a>
<a href="#cl-3646">3646</a>
<a href="#cl-3647">3647</a>
<a href="#cl-3648">3648</a>
<a href="#cl-3649">3649</a>
<a href="#cl-3650">3650</a>
<a href="#cl-3651">3651</a>
<a href="#cl-3652">3652</a>
<a href="#cl-3653">3653</a>
<a href="#cl-3654">3654</a>
<a href="#cl-3655">3655</a>
<a href="#cl-3656">3656</a>
<a href="#cl-3657">3657</a>
<a href="#cl-3658">3658</a>
<a href="#cl-3659">3659</a>
<a href="#cl-3660">3660</a>
<a href="#cl-3661">3661</a>
<a href="#cl-3662">3662</a>
<a href="#cl-3663">3663</a>
<a href="#cl-3664">3664</a>
<a href="#cl-3665">3665</a>
<a href="#cl-3666">3666</a>
<a href="#cl-3667">3667</a>
<a href="#cl-3668">3668</a>
<a href="#cl-3669">3669</a>
<a href="#cl-3670">3670</a>
<a href="#cl-3671">3671</a>
<a href="#cl-3672">3672</a>
<a href="#cl-3673">3673</a>
<a href="#cl-3674">3674</a>
<a href="#cl-3675">3675</a>
<a href="#cl-3676">3676</a>
<a href="#cl-3677">3677</a>
<a href="#cl-3678">3678</a>
<a href="#cl-3679">3679</a>
<a href="#cl-3680">3680</a>
<a href="#cl-3681">3681</a>
<a href="#cl-3682">3682</a>
<a href="#cl-3683">3683</a>
<a href="#cl-3684">3684</a>
<a href="#cl-3685">3685</a>
<a href="#cl-3686">3686</a>
<a href="#cl-3687">3687</a>
<a href="#cl-3688">3688</a>
<a href="#cl-3689">3689</a>
<a href="#cl-3690">3690</a>
<a href="#cl-3691">3691</a>
<a href="#cl-3692">3692</a>
<a href="#cl-3693">3693</a>
<a href="#cl-3694">3694</a>
<a href="#cl-3695">3695</a>
<a href="#cl-3696">3696</a>
<a href="#cl-3697">3697</a>
<a href="#cl-3698">3698</a>
<a href="#cl-3699">3699</a>
<a href="#cl-3700">3700</a>
<a href="#cl-3701">3701</a>
<a href="#cl-3702">3702</a>
<a href="#cl-3703">3703</a>
<a href="#cl-3704">3704</a>
<a href="#cl-3705">3705</a>
<a href="#cl-3706">3706</a>
<a href="#cl-3707">3707</a>
<a href="#cl-3708">3708</a>
<a href="#cl-3709">3709</a>
<a href="#cl-3710">3710</a>
<a href="#cl-3711">3711</a>
<a href="#cl-3712">3712</a>
<a href="#cl-3713">3713</a>
<a href="#cl-3714">3714</a>
<a href="#cl-3715">3715</a>
<a href="#cl-3716">3716</a>
<a href="#cl-3717">3717</a>
<a href="#cl-3718">3718</a>
<a href="#cl-3719">3719</a>
<a href="#cl-3720">3720</a>
<a href="#cl-3721">3721</a>
<a href="#cl-3722">3722</a>
<a href="#cl-3723">3723</a>
<a href="#cl-3724">3724</a>
<a href="#cl-3725">3725</a>
<a href="#cl-3726">3726</a>
<a href="#cl-3727">3727</a>
<a href="#cl-3728">3728</a>
<a href="#cl-3729">3729</a>
<a href="#cl-3730">3730</a>
<a href="#cl-3731">3731</a>
<a href="#cl-3732">3732</a>
<a href="#cl-3733">3733</a>
<a href="#cl-3734">3734</a>
<a href="#cl-3735">3735</a>
<a href="#cl-3736">3736</a>
<a href="#cl-3737">3737</a>
<a href="#cl-3738">3738</a>
<a href="#cl-3739">3739</a>
<a href="#cl-3740">3740</a>
<a href="#cl-3741">3741</a>
<a href="#cl-3742">3742</a>
<a href="#cl-3743">3743</a>
<a href="#cl-3744">3744</a>
<a href="#cl-3745">3745</a>
<a href="#cl-3746">3746</a>
<a href="#cl-3747">3747</a>
<a href="#cl-3748">3748</a>
<a href="#cl-3749">3749</a>
<a href="#cl-3750">3750</a>
<a href="#cl-3751">3751</a>
<a href="#cl-3752">3752</a>
<a href="#cl-3753">3753</a>
<a href="#cl-3754">3754</a>
<a href="#cl-3755">3755</a>
<a href="#cl-3756">3756</a>
<a href="#cl-3757">3757</a>
<a href="#cl-3758">3758</a>
<a href="#cl-3759">3759</a>
<a href="#cl-3760">3760</a>
<a href="#cl-3761">3761</a>
<a href="#cl-3762">3762</a>
<a href="#cl-3763">3763</a>
<a href="#cl-3764">3764</a>
<a href="#cl-3765">3765</a>
<a href="#cl-3766">3766</a>
<a href="#cl-3767">3767</a>
<a href="#cl-3768">3768</a>
<a href="#cl-3769">3769</a>
<a href="#cl-3770">3770</a>
<a href="#cl-3771">3771</a>
<a href="#cl-3772">3772</a>
<a href="#cl-3773">3773</a>
<a href="#cl-3774">3774</a>
<a href="#cl-3775">3775</a>
<a href="#cl-3776">3776</a>
<a href="#cl-3777">3777</a>
<a href="#cl-3778">3778</a>
<a href="#cl-3779">3779</a>
<a href="#cl-3780">3780</a>
<a href="#cl-3781">3781</a>
<a href="#cl-3782">3782</a>
<a href="#cl-3783">3783</a>
<a href="#cl-3784">3784</a>
<a href="#cl-3785">3785</a>
<a href="#cl-3786">3786</a>
<a href="#cl-3787">3787</a>
<a href="#cl-3788">3788</a>
<a href="#cl-3789">3789</a>
<a href="#cl-3790">3790</a>
<a href="#cl-3791">3791</a>
<a href="#cl-3792">3792</a>
<a href="#cl-3793">3793</a>
<a href="#cl-3794">3794</a>
<a href="#cl-3795">3795</a>
<a href="#cl-3796">3796</a>
<a href="#cl-3797">3797</a>
<a href="#cl-3798">3798</a>
<a href="#cl-3799">3799</a>
<a href="#cl-3800">3800</a>
<a href="#cl-3801">3801</a>
<a href="#cl-3802">3802</a>
<a href="#cl-3803">3803</a>
<a href="#cl-3804">3804</a>
<a href="#cl-3805">3805</a>
<a href="#cl-3806">3806</a>
<a href="#cl-3807">3807</a>
<a href="#cl-3808">3808</a>
<a href="#cl-3809">3809</a>
<a href="#cl-3810">3810</a>
<a href="#cl-3811">3811</a>
<a href="#cl-3812">3812</a>
<a href="#cl-3813">3813</a>
<a href="#cl-3814">3814</a>
<a href="#cl-3815">3815</a>
<a href="#cl-3816">3816</a>
<a href="#cl-3817">3817</a>
<a href="#cl-3818">3818</a>
<a href="#cl-3819">3819</a>
<a href="#cl-3820">3820</a>
<a href="#cl-3821">3821</a>
<a href="#cl-3822">3822</a>
<a href="#cl-3823">3823</a>
<a href="#cl-3824">3824</a>
<a href="#cl-3825">3825</a>
<a href="#cl-3826">3826</a>
<a href="#cl-3827">3827</a>
<a href="#cl-3828">3828</a>
<a href="#cl-3829">3829</a>
<a href="#cl-3830">3830</a>
<a href="#cl-3831">3831</a>
<a href="#cl-3832">3832</a>
<a href="#cl-3833">3833</a>
<a href="#cl-3834">3834</a>
<a href="#cl-3835">3835</a>
<a href="#cl-3836">3836</a>
<a href="#cl-3837">3837</a>
<a href="#cl-3838">3838</a>
<a href="#cl-3839">3839</a>
<a href="#cl-3840">3840</a>
<a href="#cl-3841">3841</a>
<a href="#cl-3842">3842</a>
<a href="#cl-3843">3843</a>
<a href="#cl-3844">3844</a>
<a href="#cl-3845">3845</a>
<a href="#cl-3846">3846</a>
<a href="#cl-3847">3847</a>
<a href="#cl-3848">3848</a>
<a href="#cl-3849">3849</a>
<a href="#cl-3850">3850</a>
<a href="#cl-3851">3851</a>
<a href="#cl-3852">3852</a>
<a href="#cl-3853">3853</a>
<a href="#cl-3854">3854</a>
<a href="#cl-3855">3855</a>
<a href="#cl-3856">3856</a>
<a href="#cl-3857">3857</a>
<a href="#cl-3858">3858</a>
<a href="#cl-3859">3859</a>
<a href="#cl-3860">3860</a>
<a href="#cl-3861">3861</a>
<a href="#cl-3862">3862</a>
<a href="#cl-3863">3863</a>
<a href="#cl-3864">3864</a>
<a href="#cl-3865">3865</a>
<a href="#cl-3866">3866</a>
<a href="#cl-3867">3867</a>
<a href="#cl-3868">3868</a>
<a href="#cl-3869">3869</a>
<a href="#cl-3870">3870</a>
<a href="#cl-3871">3871</a>
<a href="#cl-3872">3872</a>
<a href="#cl-3873">3873</a>
<a href="#cl-3874">3874</a>
<a href="#cl-3875">3875</a>
<a href="#cl-3876">3876</a>
<a href="#cl-3877">3877</a>
<a href="#cl-3878">3878</a>
<a href="#cl-3879">3879</a>
<a href="#cl-3880">3880</a>
<a href="#cl-3881">3881</a>
<a href="#cl-3882">3882</a>
<a href="#cl-3883">3883</a>
<a href="#cl-3884">3884</a>
<a href="#cl-3885">3885</a>
<a href="#cl-3886">3886</a>
<a href="#cl-3887">3887</a>
<a href="#cl-3888">3888</a>
<a href="#cl-3889">3889</a>
<a href="#cl-3890">3890</a>
<a href="#cl-3891">3891</a>
<a href="#cl-3892">3892</a>
<a href="#cl-3893">3893</a>
<a href="#cl-3894">3894</a>
<a href="#cl-3895">3895</a>
<a href="#cl-3896">3896</a>
<a href="#cl-3897">3897</a>
<a href="#cl-3898">3898</a>
<a href="#cl-3899">3899</a>
<a href="#cl-3900">3900</a>
<a href="#cl-3901">3901</a>
<a href="#cl-3902">3902</a>
<a href="#cl-3903">3903</a>
<a href="#cl-3904">3904</a>
<a href="#cl-3905">3905</a>
<a href="#cl-3906">3906</a>
<a href="#cl-3907">3907</a>
<a href="#cl-3908">3908</a>
<a href="#cl-3909">3909</a>
<a href="#cl-3910">3910</a>
<a href="#cl-3911">3911</a>
<a href="#cl-3912">3912</a>
<a href="#cl-3913">3913</a>
<a href="#cl-3914">3914</a>
<a href="#cl-3915">3915</a>
<a href="#cl-3916">3916</a>
<a href="#cl-3917">3917</a>
<a href="#cl-3918">3918</a>
<a href="#cl-3919">3919</a>
<a href="#cl-3920">3920</a>
<a href="#cl-3921">3921</a>
<a href="#cl-3922">3922</a>
<a href="#cl-3923">3923</a>
<a href="#cl-3924">3924</a>
<a href="#cl-3925">3925</a>
<a href="#cl-3926">3926</a>
<a href="#cl-3927">3927</a>
<a href="#cl-3928">3928</a>
<a href="#cl-3929">3929</a>
<a href="#cl-3930">3930</a>
<a href="#cl-3931">3931</a>
<a href="#cl-3932">3932</a>
<a href="#cl-3933">3933</a>
<a href="#cl-3934">3934</a>
<a href="#cl-3935">3935</a>
<a href="#cl-3936">3936</a>
<a href="#cl-3937">3937</a>
<a href="#cl-3938">3938</a>
<a href="#cl-3939">3939</a>
<a href="#cl-3940">3940</a>
<a href="#cl-3941">3941</a>
<a href="#cl-3942">3942</a>
<a href="#cl-3943">3943</a>
<a href="#cl-3944">3944</a>
<a href="#cl-3945">3945</a>
<a href="#cl-3946">3946</a>
<a href="#cl-3947">3947</a>
<a href="#cl-3948">3948</a>
<a href="#cl-3949">3949</a>
<a href="#cl-3950">3950</a>
<a href="#cl-3951">3951</a>
<a href="#cl-3952">3952</a>
<a href="#cl-3953">3953</a>
<a href="#cl-3954">3954</a>
<a href="#cl-3955">3955</a>
<a href="#cl-3956">3956</a>
<a href="#cl-3957">3957</a>
<a href="#cl-3958">3958</a>
<a href="#cl-3959">3959</a>
<a href="#cl-3960">3960</a>
<a href="#cl-3961">3961</a>
<a href="#cl-3962">3962</a>
<a href="#cl-3963">3963</a>
<a href="#cl-3964">3964</a>
<a href="#cl-3965">3965</a>
<a href="#cl-3966">3966</a>
<a href="#cl-3967">3967</a>
<a href="#cl-3968">3968</a>
<a href="#cl-3969">3969</a>
<a href="#cl-3970">3970</a>
<a href="#cl-3971">3971</a>
<a href="#cl-3972">3972</a>
<a href="#cl-3973">3973</a>
<a href="#cl-3974">3974</a>
<a href="#cl-3975">3975</a>
<a href="#cl-3976">3976</a>
<a href="#cl-3977">3977</a>
<a href="#cl-3978">3978</a>
<a href="#cl-3979">3979</a>
<a href="#cl-3980">3980</a>
<a href="#cl-3981">3981</a>
<a href="#cl-3982">3982</a>
<a href="#cl-3983">3983</a>
<a href="#cl-3984">3984</a>
<a href="#cl-3985">3985</a>
<a href="#cl-3986">3986</a>
<a href="#cl-3987">3987</a>
<a href="#cl-3988">3988</a>
<a href="#cl-3989">3989</a>
<a href="#cl-3990">3990</a>
<a href="#cl-3991">3991</a>
<a href="#cl-3992">3992</a>
<a href="#cl-3993">3993</a>
<a href="#cl-3994">3994</a>
<a href="#cl-3995">3995</a>
<a href="#cl-3996">3996</a>
<a href="#cl-3997">3997</a>
<a href="#cl-3998">3998</a>
<a href="#cl-3999">3999</a>
<a href="#cl-4000">4000</a>
<a href="#cl-4001">4001</a>
</pre></div></td><td class="code"><div class="highlight"><pre>
<a name="cl-1"></a>/*
<a name="cl-2"></a>Copyright(c) 2011 Nokia Siemens Network
<a name="cl-3"></a>*/
<a name="cl-4"></a>Ext.define('C8Y.ux.PanelFeatures', {
<a name="cl-5"></a>    buildTopActionMenu : function(items) {
<a name="cl-6"></a>        return {
<a name="cl-7"></a>                xtype   : 'container',
<a name="cl-8"></a>                height  : 50,
<a name="cl-9"></a>                layout  : {type: 'hbox', align:'middle'},
<a name="cl-10"></a>                itemId  : 'topDock',
<a name="cl-11"></a>                items   : items,
<a name="cl-12"></a>                dock    : 'top',
<a name="cl-13"></a>                defaults: {
<a name="cl-14"></a>                    xtype   : 'button',
<a name="cl-15"></a>                    scale   : 'medium',
<a name="cl-16"></a>                    cls     : 'action',
<a name="cl-17"></a>                    margin  : '0 5 0 0',
<a name="cl-18"></a>                    scope   : this
<a name="cl-19"></a>                }
<a name="cl-20"></a>        }
<a name="cl-21"></a>    },
<a name="cl-22"></a>    
<a name="cl-23"></a>    getDefaultGridView : function() {
<a name="cl-24"></a>        return {
<a name="cl-25"></a>            selectedItemCls : 'selectedRow'
<a name="cl-26"></a>        }
<a name="cl-27"></a>    }
<a name="cl-28"></a>});
<a name="cl-29"></a>Ext.define('C8Y.ux.plugin.Panel',{
<a name="cl-30"></a>    extend  : 'Ext.AbstractPlugin',
<a name="cl-31"></a>    alias   : 'plugin.c8ypanel',
<a name="cl-32"></a>    
<a name="cl-33"></a>    init : function(cmp) {
<a name="cl-34"></a>        this.cmp = cmp;
<a name="cl-35"></a>        this.setStyle(cmp);
<a name="cl-36"></a>        cmp.setTitle = this.setTitle;
<a name="cl-37"></a>        cmp.buildTopActionMenu = this.buildTopActionMenu;
<a name="cl-38"></a>    },
<a name="cl-39"></a>    
<a name="cl-40"></a>    setStyle : function(ref) {
<a name="cl-41"></a>         ref.padding = 20;
<a name="cl-42"></a>		 ref.style = 'background:#FFF;';
<a name="cl-43"></a>		if (!ref.windowed) ref.style = ref.style + "border: 1px solid  #B3B3B3;margin:10px 10px 0 10px;border-bottom:none";
<a name="cl-44"></a>         ref.dockedItems = ref.dockedItems || [];
<a name="cl-45"></a>         if (ref.title) {
<a name="cl-46"></a>             ref.addDocked({
<a name="cl-47"></a>                  dock   : 'top',
<a name="cl-48"></a>                  xtype  : 'container',
<a name="cl-49"></a>                  height : 40,
<a name="cl-50"></a>                  html   : Ext.String.format('&lt;h1 class="header"&gt;{0}&lt;/h1&gt;',ref.title)
<a name="cl-51"></a>              },0);
<a name="cl-52"></a>              delete ref.title;
<a name="cl-53"></a>         }
<a name="cl-54"></a>    },
<a name="cl-55"></a>    
<a name="cl-56"></a>    setTitle : function(title) {
<a name="cl-57"></a>        console.log(title);
<a name="cl-58"></a>    }
<a name="cl-59"></a>});
<a name="cl-60"></a>/**
<a name="cl-61"></a>* @class C8Y.model.Navigation
<a name="cl-62"></a>* User Model preconfigured with the defined fields
<a name="cl-63"></a>* @extends Ext.data.Model
<a name="cl-64"></a>*/
<a name="cl-65"></a>Ext.define('C8Y.model.Navigation', {
<a name="cl-66"></a>   extend       : 'Ext.data.Model',
<a name="cl-67"></a>   fields   : [
<a name="cl-68"></a>        'id',
<a name="cl-69"></a>        'name',
<a name="cl-70"></a>        {name : 'isTitle', type : 'boolean'},
<a name="cl-71"></a>        {name : 'active', type : 'boolean'}
<a name="cl-72"></a>   ]
<a name="cl-73"></a>});
<a name="cl-74"></a>/**
<a name="cl-75"></a>* @class C8Y.model.ManagedObject
<a name="cl-76"></a>* Managed Object Model with dynamic properties
<a name="cl-77"></a>* @extends Ext.data.Model
<a name="cl-78"></a>*/
<a name="cl-79"></a>Ext.define('C8Y.model.ManagedObject', {
<a name="cl-80"></a>   extend   : 'Ext.data.Model',
<a name="cl-81"></a>   requires : [
<a name="cl-82"></a>        'Ext.data.HasManyAssociation',
<a name="cl-83"></a>        'Ext.data.Field'
<a name="cl-84"></a>   ],
<a name="cl-85"></a>   idProperty   : 'id',
<a name="cl-86"></a>   
<a name="cl-87"></a>   fields   : [
<a name="cl-88"></a>        'id',
<a name="cl-89"></a>        'self',
<a name="cl-90"></a>        'type',
<a name="cl-91"></a>        'name',
<a name="cl-92"></a>        'lastUpdated'
<a name="cl-93"></a>   ],
<a name="cl-94"></a>   
<a name="cl-95"></a>   readOnly : [
<a name="cl-96"></a>        'id',
<a name="cl-97"></a>        'self'
<a name="cl-98"></a>   ],
<a name="cl-99"></a>   
<a name="cl-100"></a>   hasMany  : [
<a name="cl-101"></a>        {model: 'C8Y.model.ManagedObject', name: 'childDevices', reader  : {
<a name="cl-102"></a>            type  : 'json',
<a name="cl-103"></a>            root  : 'references'
<a name="cl-104"></a>        }},
<a name="cl-105"></a>        {model: 'C8Y.model.ManagedObject', name: 'childAssets', reader  : {
<a name="cl-106"></a>            type  : 'json',
<a name="cl-107"></a>            root  : 'references'
<a name="cl-108"></a>        }},
<a name="cl-109"></a>        {model: 'C8Y.model.ManagedObject', name: 'parents', reader  : {
<a name="cl-110"></a>            type  : 'json',
<a name="cl-111"></a>            root  : 'references'
<a name="cl-112"></a>        }}
<a name="cl-113"></a>   ],
<a name="cl-114"></a>   
<a name="cl-115"></a>   proxy    : C8Y.client.inventory.getProxy(),
<a name="cl-116"></a>   
<a name="cl-117"></a>   inheritableStatics : {
<a name="cl-118"></a>       load : function(id , callback) {
<a name="cl-119"></a>           C8Y.client.inventory.get(id, function(res) {
<a name="cl-120"></a>               var record = Ext.create(Ext.getClassName(this), res);
<a name="cl-121"></a>               callback &amp;&amp; callback(record);
<a name="cl-122"></a>           });
<a name="cl-123"></a>       }
<a name="cl-124"></a>   },
<a name="cl-125"></a>
<a name="cl-126"></a>   constructor : function(d) {
<a name="cl-127"></a>     if (d) { 
<a name="cl-128"></a>      this._hasAssets = !!(d.childAssets &amp;&amp; d.childAssets.references.length) ;
<a name="cl-129"></a>      this._hasDevices = !!(d.childDevices &amp;&amp; d.childDevices.references.length);  
<a name="cl-130"></a>     }
<a name="cl-131"></a>     
<a name="cl-132"></a>     this.callParent(arguments);
<a name="cl-133"></a>   },
<a name="cl-134"></a>   
<a name="cl-135"></a>   load : function(callback) {
<a name="cl-136"></a>       var me = this;
<a name="cl-137"></a>       C8Y.client.inventory.get(this.get('id'), function(res) {
<a name="cl-138"></a>           me.set(res)
<a name="cl-139"></a>           callback &amp;&amp; callback(res);
<a name="cl-140"></a>       });
<a name="cl-141"></a>   }, 
<a name="cl-142"></a>   
<a name="cl-143"></a>   save     : function(callback, data) {
<a name="cl-144"></a>       var me = this,
<a name="cl-145"></a>           id = this.get('id'),
<a name="cl-146"></a>           data = data || this.getChanges(),
<a name="cl-147"></a>           keysDelete = ['id', 'attrs', 'childDevices', 'childAssets'];
<a name="cl-148"></a>           
<a name="cl-149"></a>       Ext.Array.each(keysDelete, function(k){ delete data[k];});
<a name="cl-150"></a>       
<a name="cl-151"></a>       if (id) {
<a name="cl-152"></a>           C8Y.client.inventory.update(this.get('id'), data, function(res) {
<a name="cl-153"></a>              me.set(res);
<a name="cl-154"></a>              me.commit();
<a name="cl-155"></a>              callback &amp;&amp; callback();
<a name="cl-156"></a>            });
<a name="cl-157"></a>       } else {
<a name="cl-158"></a>           C8Y.client.inventory.create(data, function(res) {
<a name="cl-159"></a>               me.set(res);
<a name="cl-160"></a>               me.commit();
<a name="cl-161"></a>               callback &amp;&amp; callback();
<a name="cl-162"></a>           });
<a name="cl-163"></a>       }
<a name="cl-164"></a>   },
<a name="cl-165"></a>   
<a name="cl-166"></a>   destroy : function(options) {
<a name="cl-167"></a>       var me = this,
<a name="cl-168"></a>           newCallback;
<a name="cl-169"></a>           
<a name="cl-170"></a>       options = options || {};
<a name="cl-171"></a>       newCallback = options.success || null;
<a name="cl-172"></a>       
<a name="cl-173"></a>       options.success = function() {
<a name="cl-174"></a>           me.callStore('remove');
<a name="cl-175"></a>           newCallback &amp;&amp; newCallback();
<a name="cl-176"></a>       }
<a name="cl-177"></a>       
<a name="cl-178"></a>       this.callParent([options]);
<a name="cl-179"></a>   },
<a name="cl-180"></a>
<a name="cl-181"></a>   hasDevices : function() {
<a name="cl-182"></a>     return this._hasDevices;
<a name="cl-183"></a>   },
<a name="cl-184"></a>
<a name="cl-185"></a>   hasAssets: function() {
<a name="cl-186"></a>     return this._hasAssets;
<a name="cl-187"></a>   },
<a name="cl-188"></a>
<a name="cl-189"></a>   addDevices : function(mobjects, callback) {
<a name="cl-190"></a>       var mobjects = Ext.isArray(mobjects) ? mobjects : [mobjects],
<a name="cl-191"></a>           rawMe = this.raw,
<a name="cl-192"></a>           me = this,
<a name="cl-193"></a>           isValid;
<a name="cl-194"></a>       
<a name="cl-195"></a>       Ext.Array.each(mobjects, function(val) {
<a name="cl-196"></a>           var rawChild = val.raw;
<a name="cl-197"></a>           isValid = !!(rawChild &amp;&amp; rawMe &amp;&amp; Ext.isNumeric(rawChild.id) &amp;&amp; Ext.isNumeric(rawMe.id));
<a name="cl-198"></a>           if (isValid) {
<a name="cl-199"></a>               C8Y.client.inventory.addDevice(rawMe, rawChild, function(){
<a name="cl-200"></a>                   callback &amp;&amp; callback(me);
<a name="cl-201"></a>               });
<a name="cl-202"></a>           }
<a name="cl-203"></a>       });
<a name="cl-204"></a>       
<a name="cl-205"></a>       return isValid;
<a name="cl-206"></a>   },
<a name="cl-207"></a>
<a name="cl-208"></a>   addAssets : function(mobjects, callback) {
<a name="cl-209"></a>       var mobjects = Ext.isArray(mobjects) ? mobjects : [mobjects],
<a name="cl-210"></a>           rawMe = this.data,
<a name="cl-211"></a>           me = this,
<a name="cl-212"></a>           isValid;
<a name="cl-213"></a>       
<a name="cl-214"></a>       Ext.Array.each(mobjects, function(val) {
<a name="cl-215"></a>           var rawChild = val.data;
<a name="cl-216"></a>           isValid = !!(rawChild &amp;&amp; rawMe &amp;&amp; Ext.isNumeric(rawChild.id) &amp;&amp; Ext.isNumeric(rawMe.id));
<a name="cl-217"></a>           if (isValid) {
<a name="cl-218"></a>               C8Y.client.inventory.addAsset(rawMe, rawChild, function(){
<a name="cl-219"></a>                   callback &amp;&amp; callback(me);
<a name="cl-220"></a>               });
<a name="cl-221"></a>           }
<a name="cl-222"></a>       });
<a name="cl-223"></a>       
<a name="cl-224"></a>       return isValid;
<a name="cl-225"></a>   },
<a name="cl-226"></a>
<a name="cl-227"></a>   removeDevices : function (mobjects, callback) {
<a name="cl-228"></a>      var mobjects = Ext.isArray(mobjects) ? mobjects : [mobjects],
<a name="cl-229"></a>           id = this.get('id'),
<a name="cl-230"></a>           me = this;
<a name="cl-231"></a>       Ext.Array.each(mobjects, function(val) {
<a name="cl-232"></a>           var child = (val.get &amp;&amp; val.get('id')) || val.id || val;
<a name="cl-233"></a>           
<a name="cl-234"></a>           C8Y.client.inventory.removeDevice(id, child, function(){
<a name="cl-235"></a>              callback &amp;&amp; callback(me);
<a name="cl-236"></a>           });
<a name="cl-237"></a>      });
<a name="cl-238"></a>   },
<a name="cl-239"></a>
<a name="cl-240"></a>   removeAssets : function (mobjects, callback) {
<a name="cl-241"></a>       var mobjects = Ext.isArray(mobjects) ? mobjects : [mobjects],
<a name="cl-242"></a>           id = this.get('id'),
<a name="cl-243"></a>           me = this;
<a name="cl-244"></a>
<a name="cl-245"></a>       Ext.Array.each(mobjects, function(val) {
<a name="cl-246"></a>           var child = (val.get &amp;&amp; val.get('id')) || val.id || val;
<a name="cl-247"></a>           
<a name="cl-248"></a>           C8Y.client.inventory.removeAsset(id, child, function(){
<a name="cl-249"></a>              callback &amp;&amp; callback(me);
<a name="cl-250"></a>           });
<a name="cl-251"></a>      });
<a name="cl-252"></a>   },
<a name="cl-253"></a>
<a name="cl-254"></a>   loadDevices : function(callback) {
<a name="cl-255"></a>      var deviceStore = this.childDevices(),
<a name="cl-256"></a>          _class = 'C8Y.model.ManagedObject',
<a name="cl-257"></a>          me = this,
<a name="cl-258"></a>          deviceModels;
<a name="cl-259"></a>
<a name="cl-260"></a>      C8Y.client.inventory.listDevices(this.get('id'), function(res) {
<a name="cl-261"></a>        deviceStore.removeAll();
<a name="cl-262"></a>        deviceModels = Ext.Array.map(res.references, function(device) {
<a name="cl-263"></a>          return Ext.create(_class, device['managedObject']); 
<a name="cl-264"></a>        });
<a name="cl-265"></a>        deviceStore.add(deviceModels);
<a name="cl-266"></a>        me.fireEvent('devicesloaded', me);
<a name="cl-267"></a>        callback &amp;&amp; callback(me);
<a name="cl-268"></a>      }); 
<a name="cl-269"></a>   },
<a name="cl-270"></a>
<a name="cl-271"></a>   loadAssets : function(callback) {
<a name="cl-272"></a>      var assetStore = this.childAssets(),
<a name="cl-273"></a>          _class = 'C8Y.model.ManagedObject',
<a name="cl-274"></a>          me = this,
<a name="cl-275"></a>          assetModels;
<a name="cl-276"></a>
<a name="cl-277"></a>      C8Y.client.inventory.listAssets(this.get('id'), function(res) {
<a name="cl-278"></a>        assetStore.removeAll();
<a name="cl-279"></a>        assetModels = Ext.Array.map(res.references, function(device) {
<a name="cl-280"></a>          return Ext.create(_class, device['managedObject']); 
<a name="cl-281"></a>        });
<a name="cl-282"></a>        assetStore.add(assetModels);
<a name="cl-283"></a>        me.fireEvent('assetsloaded', me);
<a name="cl-284"></a>        callback &amp;&amp; callback(me);
<a name="cl-285"></a>      });
<a name="cl-286"></a>   }
<a name="cl-287"></a>});
<a name="cl-288"></a>
<a name="cl-289"></a>/**
<a name="cl-290"></a>* @class C8Y.store.ManagedObject
<a name="cl-291"></a>* Store Pre-configured with {@link C8Y.model.ManagedObject}
<a name="cl-292"></a>*/
<a name="cl-293"></a>Ext.define('C8Y.store.ManagedObject', {
<a name="cl-294"></a>    extend  : 'Ext.data.Store',
<a name="cl-295"></a>    model   : 'C8Y.model.ManagedObject',
<a name="cl-296"></a>    autoLoad : false,
<a name="cl-297"></a>    requires : [
<a name="cl-298"></a>        'C8Y.model.ManagedObject'
<a name="cl-299"></a>    ]
<a name="cl-300"></a>});
<a name="cl-301"></a>/**
<a name="cl-302"></a>* @class C8Y.model.UserGroup
<a name="cl-303"></a>* User Model preconfigured with the defined fields
<a name="cl-304"></a>* @extends Ext.data.Model
<a name="cl-305"></a>*/
<a name="cl-306"></a>Ext.define('C8Y.model.UserGroup', {
<a name="cl-307"></a>   extend       : 'Ext.data.Model',
<a name="cl-308"></a>   fields   : [
<a name="cl-309"></a>        { name : 'id', type : 'int'},
<a name="cl-310"></a>        'self',
<a name="cl-311"></a>        'name'
<a name="cl-312"></a>   ],
<a name="cl-313"></a>   proxy 	: C8Y.client.user.getGroupProxy(),
<a name="cl-314"></a>   hasMany  : [
<a name="cl-315"></a>        {model: 'C8Y.model.UserRole', name: 'roles', associationKey: 'roles', 
<a name="cl-316"></a>          reader  : {
<a name="cl-317"></a>            type  : 'json',
<a name="cl-318"></a>            root  : 'references',
<a name="cl-319"></a>            //To deal with the .role
<a name="cl-320"></a>            read  : function(response) {
<a name="cl-321"></a>              var data = response;
<a name="cl-322"></a>  
<a name="cl-323"></a>              if (response &amp;&amp; response.responseText) {
<a name="cl-324"></a>                data = this.getResponseData(response);
<a name="cl-325"></a>              }
<a name="cl-326"></a>        
<a name="cl-327"></a>              if (data) {
<a name="cl-328"></a>                data.references = Ext.Array.map(data.references, function(item) {
<a name="cl-329"></a>                    item = Ext.apply(item, item.role);
<a name="cl-330"></a>                    delete item.role;
<a name="cl-331"></a>                    return item;
<a name="cl-332"></a>                });
<a name="cl-333"></a>                return this.readRecords(data);
<a name="cl-334"></a>              } else {
<a name="cl-335"></a>                return this.nullResultSet;
<a name="cl-336"></a>              }
<a name="cl-337"></a>            }
<a name="cl-338"></a>          }
<a name="cl-339"></a>        }
<a name="cl-340"></a>    ],
<a name="cl-341"></a>
<a name="cl-342"></a>   load     : function(callback) {
<a name="cl-343"></a>      var me = this;
<a name="cl-344"></a>      C8Y.client.user.getGroup(this.get('id'), function(res) {
<a name="cl-345"></a>          var roles = res.roles.references;
<a name="cl-346"></a>          roles = Ext.Array.map(roles, function(role) {
<a name="cl-347"></a>            return Ext.create('C8Y.model.UserRole', role.role);
<a name="cl-348"></a>          });
<a name="cl-349"></a>          me.roles().removeAll();
<a name="cl-350"></a>          me.roles().add(roles);
<a name="cl-351"></a>          me.set(res);
<a name="cl-352"></a>          callback &amp;&amp; callback(res);
<a name="cl-353"></a>      });
<a name="cl-354"></a>      window.refUserGroup = this;
<a name="cl-355"></a>    },
<a name="cl-356"></a>    
<a name="cl-357"></a>    save     : function(callback) {
<a name="cl-358"></a>       var me = this,
<a name="cl-359"></a>           isNew = !this.get('id'),
<a name="cl-360"></a>           data = this.getChanges();
<a name="cl-361"></a>
<a name="cl-362"></a>       if (!isNew) {
<a name="cl-363"></a>           if (Ext.isEmpty(Ext.Object.getKeys(data))) {
<a name="cl-364"></a>             callback &amp;&amp; callback();
<a name="cl-365"></a>             return;
<a name="cl-366"></a>           }
<a name="cl-367"></a>           C8Y.client.user.updateGroup(this.get('id'), data, function(res) {
<a name="cl-368"></a>              me.set(res);
<a name="cl-369"></a>              me.commit();
<a name="cl-370"></a>              callback &amp;&amp; callback();
<a name="cl-371"></a>           });
<a name="cl-372"></a>       } else {
<a name="cl-373"></a>           C8Y.client.user.createGroup(data, function(res) {
<a name="cl-374"></a>               me.set(res);
<a name="cl-375"></a>               me.commit();
<a name="cl-376"></a>               callback &amp;&amp; callback();
<a name="cl-377"></a>           });
<a name="cl-378"></a>       }
<a name="cl-379"></a>    },
<a name="cl-380"></a>
<a name="cl-381"></a>	destroy : function(options) {
<a name="cl-382"></a>       var me = this,
<a name="cl-383"></a>           newCallback;
<a name="cl-384"></a>
<a name="cl-385"></a>       options = options || {};
<a name="cl-386"></a>       newCallback = options.success || null;
<a name="cl-387"></a>
<a name="cl-388"></a>       options.success = function() {
<a name="cl-389"></a>           me.callStore('remove');
<a name="cl-390"></a>           newCallback &amp;&amp; newCallback();
<a name="cl-391"></a>       }
<a name="cl-392"></a>
<a name="cl-393"></a>       this.callParent([options]);
<a name="cl-394"></a>    },
<a name="cl-395"></a>
<a name="cl-396"></a>    removeRole : function(roleid) {
<a name="cl-397"></a>      var me = this;
<a name="cl-398"></a>      //Remove group and refresh afterward
<a name="cl-399"></a>      C8Y.client.user.removeRoleFromGroup( this.get('id'), roleid, function() {
<a name="cl-400"></a>        me.fireEvent('roleremove');
<a name="cl-401"></a>        me.fireEvent('roleaction');
<a name="cl-402"></a>      });
<a name="cl-403"></a>    },
<a name="cl-404"></a>
<a name="cl-405"></a>    addRole : function(roleid) {
<a name="cl-406"></a>      var me = this,
<a name="cl-407"></a>          role = Ext.getStore('c8yuserrole').getById(roleid);
<a name="cl-408"></a>      //Add Group and refresh afterward
<a name="cl-409"></a>      C8Y.client.user.addRoleToGroup(
<a name="cl-410"></a>        this.get('id'), 
<a name="cl-411"></a>        {role: { self: role.get('self') } },
<a name="cl-412"></a>        function () {
<a name="cl-413"></a>          me.fireEvent('roleadd');
<a name="cl-414"></a>          me.fireEvent('roleaction');
<a name="cl-415"></a>        }
<a name="cl-416"></a>      );
<a name="cl-417"></a>    },
<a name="cl-418"></a>
<a name="cl-419"></a>    updateRoles : function(selroles) {
<a name="cl-420"></a>      var currentroles = this.getRolesIdArray(),
<a name="cl-421"></a>        itemstodelete = [],
<a name="cl-422"></a>        itemstoadd    = [],
<a name="cl-423"></a>        me = this,
<a name="cl-424"></a>        removecount = 0,
<a name="cl-425"></a>        addcount = 0,
<a name="cl-426"></a>        roleaction = 0,
<a name="cl-427"></a>        callback = function() {
<a name="cl-428"></a>          roleaction--;
<a name="cl-429"></a>          if (!roleaction) {
<a name="cl-430"></a>            me.un('roleaction', callback);
<a name="cl-431"></a>            me.fireEvent('roleactioncomplete');
<a name="cl-432"></a>            me.load();
<a name="cl-433"></a>          }
<a name="cl-434"></a>        };
<a name="cl-435"></a>      
<a name="cl-436"></a>      Ext.Array.each(selroles, function(item) {
<a name="cl-437"></a>          if (!Ext.Array.contains(currentroles, item)) {
<a name="cl-438"></a>            itemstoadd.push(item);
<a name="cl-439"></a>          }
<a name="cl-440"></a>      });
<a name="cl-441"></a>
<a name="cl-442"></a>      Ext.Array.each(currentroles, function(item) {
<a name="cl-443"></a>          if (!Ext.Array.contains(selroles, item)) {
<a name="cl-444"></a>            itemstodelete.push(item);
<a name="cl-445"></a>          }
<a name="cl-446"></a>      });
<a name="cl-447"></a>      
<a name="cl-448"></a>      Ext.Array.each(itemstodelete, function(item) {
<a name="cl-449"></a>          removecount++;
<a name="cl-450"></a>          roleaction++;
<a name="cl-451"></a>          me.removeRole(item);
<a name="cl-452"></a>      });
<a name="cl-453"></a>
<a name="cl-454"></a>      Ext.Array.each(itemstoadd, function(item) {
<a name="cl-455"></a>          addcount++;
<a name="cl-456"></a>          roleaction++;
<a name="cl-457"></a>          me.addRole(item);
<a name="cl-458"></a>      });
<a name="cl-459"></a>
<a name="cl-460"></a>      if (roleaction) {
<a name="cl-461"></a>        me.on('roleaction', callback );             
<a name="cl-462"></a>      }
<a name="cl-463"></a>
<a name="cl-464"></a>      return !!roleaction;
<a name="cl-465"></a>    },
<a name="cl-466"></a>
<a name="cl-467"></a>    getRolesIdArray   : function() {
<a name="cl-468"></a>      var items = [];
<a name="cl-469"></a>      this.roles().each(function(item) {
<a name="cl-470"></a>        items.push(item.get('id'));
<a name="cl-471"></a>      });
<a name="cl-472"></a>      return items;
<a name="cl-473"></a>    }
<a name="cl-474"></a>});
<a name="cl-475"></a>/**
<a name="cl-476"></a>* @class C8Y.store.UserGroup
<a name="cl-477"></a>* Store Pre-configured with {@link C8Y.model.User}
<a name="cl-478"></a>*/
<a name="cl-479"></a>Ext.define('C8Y.store.UserGroup', {
<a name="cl-480"></a>    extend  : 'Ext.data.Store',
<a name="cl-481"></a>    model   : 'C8Y.model.UserGroup',
<a name="cl-482"></a>    autoLoad : false,
<a name="cl-483"></a>    pageSize: 1500,
<a name="cl-484"></a>    requires : [
<a name="cl-485"></a>        'C8Y.model.UserGroup'
<a name="cl-486"></a>    ]
<a name="cl-487"></a>});
<a name="cl-488"></a>/**
<a name="cl-489"></a>* @class C8Y.app.Footer
<a name="cl-490"></a>* Footer for the default application viewport
<a name="cl-491"></a>* @extends Ext.container.Container
<a name="cl-492"></a>* @xtype c8yfooter
<a name="cl-493"></a>*/
<a name="cl-494"></a>Ext.define('C8Y.app.Footer', {
<a name="cl-495"></a>    
<a name="cl-496"></a>    extend  : 'Ext.container.Container',
<a name="cl-497"></a>    
<a name="cl-498"></a>    alias   : 'widget.c8yfooter',
<a name="cl-499"></a>    
<a name="cl-500"></a>    //Overrides eventual configuration passed at instantiation time
<a name="cl-501"></a>    initComponent : function() {
<a name="cl-502"></a>        this.height = 38;
<a name="cl-503"></a>        this.cls = 'C8YFooter';
<a name="cl-504"></a>        this.layout = {type:'hbox', align:'middle'};
<a name="cl-505"></a>        this.defaults = {xtype: 'button'};
<a name="cl-506"></a>        this.items = this.buildItems();
<a name="cl-507"></a>        this.callParent(arguments);
<a name="cl-508"></a>    },
<a name="cl-509"></a>    
<a name="cl-510"></a>    buildItems : function() {
<a name="cl-511"></a>        var btns = this.buildButtons();
<a name="cl-512"></a>        return btns.concat([
<a name="cl-513"></a>            {
<a name="cl-514"></a>                flex :  1,
<a name="cl-515"></a>                xtype: 'container',
<a name="cl-516"></a>                html : '&amp;copy; 2011 Nokia Siemens Network All rights Reserved',
<a name="cl-517"></a>                style: 'text-align:right',
<a name="cl-518"></a>                margin: '0 10 0 0'
<a name="cl-519"></a>            }
<a name="cl-520"></a>        ]);
<a name="cl-521"></a>    },
<a name="cl-522"></a>    
<a name="cl-523"></a>    buildButtons : function() {
<a name="cl-524"></a>        var nbtns = [],
<a name="cl-525"></a>            btns = [
<a name="cl-526"></a>            { text  : 'About Cumulocity'},
<a name="cl-527"></a>            { text  : 'Support'},
<a name="cl-528"></a>            { text  : 'Legal'},
<a name="cl-529"></a>            { text  : 'Privacy policy'},
<a name="cl-530"></a>            { text  : 'Terms of use'},
<a name="cl-531"></a>            { text  : 'Contact us'}
<a name="cl-532"></a>        ]
<a name="cl-533"></a>        
<a name="cl-534"></a>        for (var i=0, t=btns.length;i&lt;t;i++) {
<a name="cl-535"></a>            btns[i].cls = 'C8Y-btnsimple';
<a name="cl-536"></a>            if (i==0) {
<a name="cl-537"></a>                btns[i].margin = '0 0 0 10';
<a name="cl-538"></a>            }
<a name="cl-539"></a>            nbtns.push(btns[i]);
<a name="cl-540"></a>            if (i&lt;(t-1)) {
<a name="cl-541"></a>                nbtns.push({
<a name="cl-542"></a>                    xtype : 'container',
<a name="cl-543"></a>                    html  : '|',
<a name="cl-544"></a>					cls   : 'C8Y-purple'
<a name="cl-545"></a>                });
<a name="cl-546"></a>            }
<a name="cl-547"></a>        }
<a name="cl-548"></a>        
<a name="cl-549"></a>        return nbtns;
<a name="cl-550"></a>    }
<a name="cl-551"></a>});
<a name="cl-552"></a>/**
<a name="cl-553"></a>* @class C8Y.app.Header
<a name="cl-554"></a>* Header for the application view port. Contains logo and information about active user.
<a name="cl-555"></a>* @extends Ext.container.Container
<a name="cl-556"></a>* @xtype C8Yheader
<a name="cl-557"></a>*/
<a name="cl-558"></a>Ext.define('C8Y.app.Header', {
<a name="cl-559"></a>    
<a name="cl-560"></a>    extend  : 'Ext.container.Container',
<a name="cl-561"></a>
<a name="cl-562"></a>    requires : [
<a name="cl-563"></a>        'Ext.layout.container.HBox',
<a name="cl-564"></a>        'Ext.toolbar.Spacer',
<a name="cl-565"></a>        'Ext.toolbar.Separator',
<a name="cl-566"></a>		'Ext.fx.Anim'
<a name="cl-567"></a>    ],
<a name="cl-568"></a>    
<a name="cl-569"></a>    alias   : 'widget.c8yheader',
<a name="cl-570"></a>    
<a name="cl-571"></a>    //Overrides eventual configuration passed at instantiation time
<a name="cl-572"></a>    initComponent : function() {
<a name="cl-573"></a>      this.height = 65;
<a name="cl-574"></a>      this.layout = {type: 'hbox', align:'middle'};
<a name="cl-575"></a>      this.cls = 'C8Yheader';
<a name="cl-576"></a>      this.defaults = {xtype:'container'};
<a name="cl-577"></a>      this.items = this.buildItems();
<a name="cl-578"></a>      this.callParent(arguments);
<a name="cl-579"></a>	  this.setVisible(false);
<a name="cl-580"></a>      C8Y.client.evtbus.addListener('login', Ext.Function.bind(this.onLogin, this));
<a name="cl-581"></a>    },
<a name="cl-582"></a>    
<a name="cl-583"></a>    buildItems : function() {
<a name="cl-584"></a>        return [
<a name="cl-585"></a>            {
<a name="cl-586"></a>                itemId  : 'logoproduct',
<a name="cl-587"></a>                cls     : 'C8Yheaderlogoproduct',
<a name="cl-588"></a>                width   : 246,
<a name="cl-589"></a>                height  : 65,
<a name="cl-590"></a>                margin  : '0 20 0 0'
<a name="cl-591"></a>            },
<a name="cl-592"></a>            this.buildUserInfo(),
<a name="cl-593"></a>            this.buildAlarmInfo(),
<a name="cl-594"></a>            {
<a name="cl-595"></a>                xtype   : 'container',
<a name="cl-596"></a>                width   : 246,
<a name="cl-597"></a>                height  : 65,
<a name="cl-598"></a>                itemId  : 'logo',
<a name="cl-599"></a>                cls     : 'C8Yheaderlogo',
<a name="cl-600"></a>                items   : this.buildNavLinks(),
<a name="cl-601"></a>                layout  : {type: 'hbox', align:'middle'},
<a name="cl-602"></a>                defaults: {xtype:'button'},
<a name="cl-603"></a>                padding : 5,
<a name="cl-604"></a>                margin  : '0 0 0 20'
<a name="cl-605"></a>            }
<a name="cl-606"></a>        ]; 
<a name="cl-607"></a>    },
<a name="cl-608"></a>    
<a name="cl-609"></a>    buildUserInfo : function() {
<a name="cl-610"></a>        return  {
<a name="cl-611"></a>            xtype       : 'button',
<a name="cl-612"></a>            text        : 'Welcome, &lt;b&gt;Andre Koeman&lt;/b&gt;',
<a name="cl-613"></a>            // iconCls     : 'iconPurpleArrowdown',
<a name="cl-614"></a>            // iconAlign   : 'right',
<a name="cl-615"></a>            itemId      : 'userBtn',
<a name="cl-616"></a>            hidden      : true,
<a name="cl-617"></a>            cls         : 'C8Y-btnsimple c8ywelcome',
<a name="cl-618"></a>            menu    : { 
<a name="cl-619"></a>                plain  : true,
<a name="cl-620"></a>                defaults : {'cls': 'action', width:150}, 
<a name="cl-621"></a>                items : [
<a name="cl-622"></a>                    { text : 'User Settings', scope: this, handler: this.onUserSettings},
<a name="cl-623"></a>                    { text : 'Logout', scope: this, handler: this.onLogout }
<a name="cl-624"></a>                ]
<a name="cl-625"></a>            }
<a name="cl-626"></a>        }
<a name="cl-627"></a>    },
<a name="cl-628"></a>    
<a name="cl-629"></a>    buildAlarmInfo : function() {
<a name="cl-630"></a>        return {
<a name="cl-631"></a>            xype    : 'container',
<a name="cl-632"></a>            // cls     : 'C8Yheaderalarms',
<a name="cl-633"></a>            itemId  : 'alarms',
<a name="cl-634"></a>            visible : false,
<a name="cl-635"></a>            flex    : 1,
<a name="cl-636"></a>            margin  : '0 0 0 20',
<a name="cl-637"></a>            defaults: { xtype   : 'button'},
<a name="cl-638"></a>            items   : [
<a name="cl-639"></a>                //Alarms Items
<a name="cl-640"></a>                // {
<a name="cl-641"></a>                //                     text    : '(5) new promotions',
<a name="cl-642"></a>                //                     cls     : 'C8Y-btnsimple'
<a name="cl-643"></a>                //                 },
<a name="cl-644"></a>                //                 {
<a name="cl-645"></a>                //                     text    : '(1) new vending machine',
<a name="cl-646"></a>                //                     cls     : 'C8Y-btnsimple'
<a name="cl-647"></a>                //                 },
<a name="cl-648"></a>                //                 {
<a name="cl-649"></a>                //                     text    : '(1) new clients',
<a name="cl-650"></a>                //                     cls     : 'C8Y-btnsimple'
<a name="cl-651"></a>                //                 }
<a name="cl-652"></a>            ]
<a name="cl-653"></a>        }
<a name="cl-654"></a>    },
<a name="cl-655"></a>    
<a name="cl-656"></a>    buildNavLinks : function() {
<a name="cl-657"></a>        return [
<a name="cl-658"></a>             {
<a name="cl-659"></a>                 text    : 'Help',
<a name="cl-660"></a>                 cls     : 'C8Y-btnsimple'
<a name="cl-661"></a>             },
<a name="cl-662"></a>             {
<a name="cl-663"></a>                 xtype  : 'container',
<a name="cl-664"></a>                 cls    : 'C8Y-purple',
<a name="cl-665"></a>                 html   : '|',
<a name="cl-666"></a>                 itemId : 'logoutDash',
<a name="cl-667"></a>                 hidden : true
<a name="cl-668"></a>             },
<a name="cl-669"></a>             {
<a name="cl-670"></a>                  text    : 'Logout',
<a name="cl-671"></a>                  itemId  : 'logoutBtn',
<a name="cl-672"></a>                  hidden  : true,
<a name="cl-673"></a>                  cls     : 'C8Y-btnsimple',
<a name="cl-674"></a>                  scope   : this,
<a name="cl-675"></a>                  handler : this.onLogout
<a name="cl-676"></a>             }
<a name="cl-677"></a>        ];
<a name="cl-678"></a>    },
<a name="cl-679"></a>    
<a name="cl-680"></a>    doLogout : function() {
<a name="cl-681"></a>        var logo = this.getComponent('logo'),
<a name="cl-682"></a>            logoutDash = logo.getComponent('logoutDash'),
<a name="cl-683"></a>            logoutBtn = logo.getComponent('logoutBtn'),
<a name="cl-684"></a>            userBtn = this.getComponent('userBtn');
<a name="cl-685"></a>        
<a name="cl-686"></a>        userBtn.hide();
<a name="cl-687"></a>        logoutDash.hide();
<a name="cl-688"></a>        logoutBtn.hide();
<a name="cl-689"></a>		this.setVisible(false);
<a name="cl-690"></a>        if (this.app) {
<a name="cl-691"></a>            this.app.fireEvent('logout');
<a name="cl-692"></a>        }
<a name="cl-693"></a>    },
<a name="cl-694"></a>    
<a name="cl-695"></a>    onLogin : function(uData) {
<a name="cl-696"></a>        var logo = this.getComponent('logo'),
<a name="cl-697"></a>            logoutDash = logo.getComponent('logoutDash'),
<a name="cl-698"></a>            logoutBtn = logo.getComponent('logoutBtn'),
<a name="cl-699"></a>            userBtn = this.getComponent('userBtn'),
<a name="cl-700"></a>			name = (uData.firstName &amp;&amp; uData.lastName) ? (uData.firstName + ' ' + uData.lastName) : uData.userName;
<a name="cl-701"></a>        
<a name="cl-702"></a>        userBtn.setText(Ext.String.format('Welcome, &lt;b&gt;{0}&lt;/b&gt;', name));
<a name="cl-703"></a>        userBtn.show();
<a name="cl-704"></a>        logoutDash.show();
<a name="cl-705"></a>        logoutBtn.show();
<a name="cl-706"></a>		this.setVisible(true);
<a name="cl-707"></a>    },
<a name="cl-708"></a>    
<a name="cl-709"></a>    onLogout : function() {
<a name="cl-710"></a>        var me = this;
<a name="cl-711"></a>        Ext.Msg.show({
<a name="cl-712"></a>             title  : 'Logout',
<a name="cl-713"></a>             msg    : 'Do you wish to logout?',
<a name="cl-714"></a>             buttons: Ext.Msg.YESNO,
<a name="cl-715"></a>             icon   : Ext.Msg.QUESTION,
<a name="cl-716"></a>             fn     : function(btn) {
<a name="cl-717"></a>                 if (btn == 'yes') {
<a name="cl-718"></a>                     me.doLogout();
<a name="cl-719"></a>                 } 
<a name="cl-720"></a>             }
<a name="cl-721"></a>        });
<a name="cl-722"></a>    },
<a name="cl-723"></a>    
<a name="cl-724"></a>    onUserSettings : function() {
<a name="cl-725"></a>        var user = C8Y.client.getUser(),
<a name="cl-726"></a>            model = Ext.create('C8Y.model.User',{
<a name="cl-727"></a>                userName : user.userName
<a name="cl-728"></a>            }),
<a name="cl-729"></a>            title = 'Edit User Settings',
<a name="cl-730"></a>            form = Ext.create('C8Y.ux.UserForm', {
<a name="cl-731"></a>                windowed: true,
<a name="cl-732"></a>                title   : title,
<a name="cl-733"></a>                listeners : {
<a name="cl-734"></a>                    'render' : function(form) {
<a name="cl-735"></a>                        form.loadUser(model);
<a name="cl-736"></a>                    }
<a name="cl-737"></a>                }
<a name="cl-738"></a>            });
<a name="cl-739"></a>    }
<a name="cl-740"></a>});
<a name="cl-741"></a>Ext.define('C8Y.ux.UserRoleFieldSet', {
<a name="cl-742"></a>	extend		: 'Ext.form.FieldSet',
<a name="cl-743"></a>	alias		: 'widget.c8yuserrolefieldset',
<a name="cl-744"></a>	requires	: [
<a name="cl-745"></a>		'Ext.form.FieldSet',
<a name="cl-746"></a>		'Ext.form.field.Checkbox',
<a name="cl-747"></a>		'Ext.form.FieldContainer'
<a name="cl-748"></a>	],
<a name="cl-749"></a>
<a name="cl-750"></a>	initComponent : function() {
<a name="cl-751"></a>		this.title = "Roles";
<a name="cl-752"></a>		this.items = {
<a name="cl-753"></a>			xtype		: 'fieldcontainer',
<a name="cl-754"></a>			items 		: [],
<a name="cl-755"></a>			defaults 	: {
<a name="cl-756"></a>				xtype		: 'checkbox'
<a name="cl-757"></a>			},
<a name="cl-758"></a>			items		: this.buildItems(),
<a name="cl-759"></a>			itemId		: 'fcont'
<a name="cl-760"></a>		};
<a name="cl-761"></a>		this.buildItems();
<a name="cl-762"></a>		this.callParent(arguments);
<a name="cl-763"></a>	},
<a name="cl-764"></a>
<a name="cl-765"></a>	buildItems : function() {
<a name="cl-766"></a>		var components = [],
<a name="cl-767"></a>			roles = Ext.getStore('c8yuserrole');
<a name="cl-768"></a>
<a name="cl-769"></a>		roles.each(function(role) {
<a name="cl-770"></a>			components.push({
<a name="cl-771"></a>				boxLabel	: role.get('name').replace(/ROLE_/,''),
<a name="cl-772"></a>				name		: 'roles',
<a name="cl-773"></a>				inputValue	: role.get('id')
<a name="cl-774"></a>			});	
<a name="cl-775"></a>		});
<a name="cl-776"></a>		return components;
<a name="cl-777"></a>	}
<a name="cl-778"></a>});
<a name="cl-779"></a>/**
<a name="cl-780"></a>* @class C8Y.model.UserRole
<a name="cl-781"></a>* User Model preconfigured with the defined fields
<a name="cl-782"></a>* @extends Ext.data.Model
<a name="cl-783"></a>*/
<a name="cl-784"></a>Ext.define('C8Y.model.UserRole', {
<a name="cl-785"></a>   extend       : 'Ext.data.Model',
<a name="cl-786"></a>   requires		: [
<a name="cl-787"></a>      'Ext.data.proxy.Memory'
<a name="cl-788"></a>   ],
<a name="cl-789"></a>   fields   : [
<a name="cl-790"></a>        { name : 'id', type : 'int'},
<a name="cl-791"></a>        'self',
<a name="cl-792"></a>        'name'
<a name="cl-793"></a>   ],
<a name="cl-794"></a>   /*proxy: {
<a name="cl-795"></a>        type 	: 'memory',
<a name="cl-796"></a>        reader  : {
<a name="cl-797"></a>        	type	: 'json',
<a name="cl-798"></a>        	root	: 'references'
<a name="cl-799"></a>        } 
<a name="cl-800"></a>   }*/
<a name="cl-801"></a>   proxy	: C8Y.client.user.getRoleProxy()
<a name="cl-802"></a>});
<a name="cl-803"></a>/**
<a name="cl-804"></a>* @class C8Y.store.UserRole
<a name="cl-805"></a>* Store Pre-configured with {@link C8Y.model.User}
<a name="cl-806"></a>*/
<a name="cl-807"></a>Ext.define('C8Y.store.UserRole', {
<a name="cl-808"></a>    extend  : 'Ext.data.Store',
<a name="cl-809"></a>    model   : 'C8Y.model.UserRole',
<a name="cl-810"></a>    autoLoad : false,
<a name="cl-811"></a>    pageSize: 1500,
<a name="cl-812"></a>    requires : [
<a name="cl-813"></a>        'C8Y.model.UserRole'
<a name="cl-814"></a>    ]
<a name="cl-815"></a>});
<a name="cl-816"></a>/**
<a name="cl-817"></a>* @class C8Y.model.User
<a name="cl-818"></a>* User Model preconfigured with the defined fields
<a name="cl-819"></a>* @extends Ext.data.Model
<a name="cl-820"></a>*/
<a name="cl-821"></a>Ext.define('C8Y.model.User', {
<a name="cl-822"></a>   extend       : 'Ext.data.Model',
<a name="cl-823"></a>   idProperty   : 'userName',
<a name="cl-824"></a>   requires : [
<a name="cl-825"></a>        'Ext.data.HasManyAssociation',
<a name="cl-826"></a>        'Ext.data.Field',
<a name="cl-827"></a>        'C8Y.model.UserRole',
<a name="cl-828"></a>        'C8Y.model.UserGroup'
<a name="cl-829"></a>   ],
<a name="cl-830"></a>   fields   : [
<a name="cl-831"></a>        'id',
<a name="cl-832"></a>        'email',
<a name="cl-833"></a>        { name : 'enabled', type : 'boolean'},
<a name="cl-834"></a>        'userName',
<a name="cl-835"></a>        'firstName',
<a name="cl-836"></a>        'lastName',
<a name="cl-837"></a>        'password',
<a name="cl-838"></a>        'phone',
<a name="cl-839"></a>        'self'
<a name="cl-840"></a>   ],
<a name="cl-841"></a>
<a name="cl-842"></a>   hasMany  : [
<a name="cl-843"></a>        {model: 'C8Y.model.UserRole', name: 'roles', associationKey: 'roles', 
<a name="cl-844"></a>          reader  : {
<a name="cl-845"></a>            type  : 'json',
<a name="cl-846"></a>            root  : 'references',
<a name="cl-847"></a>            //To deal with the .role
<a name="cl-848"></a>            read  : function(response) {
<a name="cl-849"></a>              var data = response;
<a name="cl-850"></a>  
<a name="cl-851"></a>              if (response &amp;&amp; response.responseText) {
<a name="cl-852"></a>                data = this.getResponseData(response);
<a name="cl-853"></a>              }
<a name="cl-854"></a>        
<a name="cl-855"></a>              if (data) {
<a name="cl-856"></a>                data.references = Ext.Array.map(data.references, function(item) {
<a name="cl-857"></a>                    item = Ext.apply(item, item.role);
<a name="cl-858"></a>                    delete item.role;
<a name="cl-859"></a>                    return item;
<a name="cl-860"></a>                });
<a name="cl-861"></a>                return this.readRecords(data);
<a name="cl-862"></a>              } else {
<a name="cl-863"></a>                return this.nullResultSet;
<a name="cl-864"></a>              }
<a name="cl-865"></a>            }
<a name="cl-866"></a>          }
<a name="cl-867"></a>        },
<a name="cl-868"></a>        {model: 'C8Y.model.UserGroup', name: 'groups', associationKey: 'groups',
<a name="cl-869"></a>          reader : {
<a name="cl-870"></a>              type  : 'json',
<a name="cl-871"></a>              root  : 'references',
<a name="cl-872"></a>              //To deal with the .group
<a name="cl-873"></a>              read  : function(response) {
<a name="cl-874"></a>                var data = response;
<a name="cl-875"></a>    
<a name="cl-876"></a>                if (response &amp;&amp; response.responseText) {
<a name="cl-877"></a>                  data = this.getResponseData(response);
<a name="cl-878"></a>                }
<a name="cl-879"></a>          
<a name="cl-880"></a>                if (data) {
<a name="cl-881"></a>                  data.references = Ext.Array.map(data.references, function(item) {
<a name="cl-882"></a>                      item = Ext.apply(item, item.group);
<a name="cl-883"></a>                      delete item.group;
<a name="cl-884"></a>                      return item;
<a name="cl-885"></a>                  });
<a name="cl-886"></a>                  return this.readRecords(data);
<a name="cl-887"></a>                } else {
<a name="cl-888"></a>                  return this.nullResultSet;
<a name="cl-889"></a>                }
<a name="cl-890"></a>            }
<a name="cl-891"></a>          }
<a name="cl-892"></a>        }
<a name="cl-893"></a>   ],
<a name="cl-894"></a>   
<a name="cl-895"></a>   proxy    : C8Y.client.user.getProxy(),
<a name="cl-896"></a>      
<a name="cl-897"></a>   load     : function(callback) {
<a name="cl-898"></a>      var me = this,
<a name="cl-899"></a>          callcount = 0,
<a name="cl-900"></a>          callbacks = [],
<a name="cl-901"></a>          //2 server calls: the callback should only be fired after the 2 are complete
<a name="cl-902"></a>          multicallback = function(res) {
<a name="cl-903"></a>            callcount++;
<a name="cl-904"></a>            if (callcount == 2) {
<a name="cl-905"></a>              Ext.Array.each(callbacks, function(fn) {
<a name="cl-906"></a>                  fn();
<a name="cl-907"></a>              });
<a name="cl-908"></a>              callback &amp;&amp; callback(me);
<a name="cl-909"></a>            }
<a name="cl-910"></a>          };
<a name="cl-911"></a>	  window.refUser = this;
<a name="cl-912"></a>	
<a name="cl-913"></a>      C8Y.client.user.get(this.get('userName'), function(res) {
<a name="cl-914"></a>          delete res.groups;
<a name="cl-915"></a>          callbacks[1] = (function() {
<a name="cl-916"></a>			  var roles = res.roles.references;
<a name="cl-917"></a>			  roles = Ext.Array.map(roles, function(role) {
<a name="cl-918"></a>			 	return Ext.create('C8Y.model.UserRole', role.role);
<a name="cl-919"></a>			  });
<a name="cl-920"></a>			  me.roles().removeAll();
<a name="cl-921"></a>			  me.roles().add(roles);
<a name="cl-922"></a>			  me.set(res);
<a name="cl-923"></a>          });
<a name="cl-924"></a>          multicallback(res);
<a name="cl-925"></a>      });
<a name="cl-926"></a>
<a name="cl-927"></a>      C8Y.client.user.getUserGroups(this.get('userName'), function(res) {
<a name="cl-928"></a>          callbacks[0] = (function() {
<a name="cl-929"></a>            var newgroups = Ext.Array.map(res.references, function(item) {
<a name="cl-930"></a>              return Ext.create('C8Y.model.UserGroup', item.group);
<a name="cl-931"></a>            });
<a name="cl-932"></a>            me.groups().removeAll();
<a name="cl-933"></a>            me.groups().add(newgroups);
<a name="cl-934"></a>          });
<a name="cl-935"></a>          multicallback(res);    
<a name="cl-936"></a>      });
<a name="cl-937"></a>    },
<a name="cl-938"></a>    
<a name="cl-939"></a>    save     : function(callback) {
<a name="cl-940"></a>       var me = this,
<a name="cl-941"></a>           isNew = !this.get('id'),
<a name="cl-942"></a>           data = this.getChanges();
<a name="cl-943"></a>
<a name="cl-944"></a>       if (!isNew) {
<a name="cl-945"></a>           if (Ext.isEmpty(Ext.Object.getKeys(data))) {
<a name="cl-946"></a>             callback &amp;&amp; callback();
<a name="cl-947"></a>             return;
<a name="cl-948"></a>           }
<a name="cl-949"></a>           C8Y.client.user.update(this.get('userName'), data, function(res) {
<a name="cl-950"></a>              me.set(res);
<a name="cl-951"></a>              me.commit();
<a name="cl-952"></a>              callback &amp;&amp; callback();
<a name="cl-953"></a>            });
<a name="cl-954"></a>       } else {
<a name="cl-955"></a>           C8Y.client.user.create(data, function(res) {
<a name="cl-956"></a>               me.set(res);
<a name="cl-957"></a>               me.commit();
<a name="cl-958"></a>               callback &amp;&amp; callback();
<a name="cl-959"></a>           });
<a name="cl-960"></a>       }
<a name="cl-961"></a>    },
<a name="cl-962"></a>    
<a name="cl-963"></a>    disable : function() {
<a name="cl-964"></a>        var me = this;
<a name="cl-965"></a>        this.set('enabled', false);
<a name="cl-966"></a>        this.save(function() {
<a name="cl-967"></a>            me.commit();
<a name="cl-968"></a>        });
<a name="cl-969"></a>    },
<a name="cl-970"></a>    
<a name="cl-971"></a>    destroy : function(options) {
<a name="cl-972"></a>       var me = this,
<a name="cl-973"></a>           newCallback;
<a name="cl-974"></a>
<a name="cl-975"></a>       options = options || {};
<a name="cl-976"></a>       newCallback = options.success || null;
<a name="cl-977"></a>
<a name="cl-978"></a>       options.success = function() {
<a name="cl-979"></a>           me.callStore('remove');
<a name="cl-980"></a>           newCallback &amp;&amp; newCallback();
<a name="cl-981"></a>       };
<a name="cl-982"></a>
<a name="cl-983"></a>       this.callParent([options]);
<a name="cl-984"></a>    },
<a name="cl-985"></a>
<a name="cl-986"></a>    removeGroup : function(group) {
<a name="cl-987"></a>      var me = this;
<a name="cl-988"></a>      group = Ext.isNumeric(group) ? group : group.get('id');
<a name="cl-989"></a>      //Remove group and refresh afterward
<a name="cl-990"></a>      C8Y.client.user.removeFromGroup(this.get('id'), group, function() {
<a name="cl-991"></a>        me.fireEvent('groupremove');
<a name="cl-992"></a>        me.fireEvent('groupaction');
<a name="cl-993"></a>      });
<a name="cl-994"></a>    },
<a name="cl-995"></a>
<a name="cl-996"></a>    addGroup : function(group) {
<a name="cl-997"></a>      var me = this;
<a name="cl-998"></a>      group = Ext.isNumeric(group) ? group : group.get('id');
<a name="cl-999"></a>      //Add Group and refresh afterward
<a name="cl-1000"></a>      C8Y.client.user.addToGroup(
<a name="cl-1001"></a>        group, 
<a name="cl-1002"></a>        {user: { id: this.get('id'), userName: this.get('userName'), self: this.get('self')} },
<a name="cl-1003"></a>        function () {
<a name="cl-1004"></a>          me.fireEvent('groupadd');
<a name="cl-1005"></a>          me.fireEvent('groupaction');
<a name="cl-1006"></a>        }
<a name="cl-1007"></a>      );
<a name="cl-1008"></a>    },
<a name="cl-1009"></a>
<a name="cl-1010"></a>    updateGroups : function(selgroups) {
<a name="cl-1011"></a>      var currentgroups = this.getGroupsIdArray(),
<a name="cl-1012"></a>        itemstodelete = [],
<a name="cl-1013"></a>        itemstoadd    = [],
<a name="cl-1014"></a>        me = this,
<a name="cl-1015"></a>        removecount = 0,
<a name="cl-1016"></a>        addcount = 0,
<a name="cl-1017"></a>        groupaction = 0,
<a name="cl-1018"></a>        callback = function() {
<a name="cl-1019"></a>          groupaction--;
<a name="cl-1020"></a>          if (!groupaction) {
<a name="cl-1021"></a>            me.un('groupaction', callback);
<a name="cl-1022"></a>            me.fireEvent('groupactioncomplete');
<a name="cl-1023"></a>            me.load();
<a name="cl-1024"></a>          }
<a name="cl-1025"></a>        };
<a name="cl-1026"></a>      
<a name="cl-1027"></a>      Ext.Array.each(selgroups, function(item) {
<a name="cl-1028"></a>          if (!Ext.Array.contains(currentgroups, item)) {
<a name="cl-1029"></a>            itemstoadd.push(item);
<a name="cl-1030"></a>          }
<a name="cl-1031"></a>      });
<a name="cl-1032"></a>
<a name="cl-1033"></a>      Ext.Array.each(currentgroups, function(item) {
<a name="cl-1034"></a>          if (!Ext.Array.contains(selgroups, item)) {
<a name="cl-1035"></a>            itemstodelete.push(item);
<a name="cl-1036"></a>          }
<a name="cl-1037"></a>      });
<a name="cl-1038"></a>      
<a name="cl-1039"></a>      Ext.Array.each(itemstodelete, function(item) {
<a name="cl-1040"></a>          removecount++;
<a name="cl-1041"></a>          groupaction++;
<a name="cl-1042"></a>          me.removeGroup(item);
<a name="cl-1043"></a>      });
<a name="cl-1044"></a>
<a name="cl-1045"></a>      Ext.Array.each(itemstoadd, function(item) {
<a name="cl-1046"></a>          addcount++;
<a name="cl-1047"></a>          groupaction++;
<a name="cl-1048"></a>          me.addGroup(item);
<a name="cl-1049"></a>      });
<a name="cl-1050"></a>
<a name="cl-1051"></a>      if (groupaction) {
<a name="cl-1052"></a>        me.on('groupaction', callback );             
<a name="cl-1053"></a>      }
<a name="cl-1054"></a>
<a name="cl-1055"></a>      return !!groupaction;
<a name="cl-1056"></a>    },
<a name="cl-1057"></a>
<a name="cl-1058"></a>    getGroupsIdArray   : function() {
<a name="cl-1059"></a>      var items = [];
<a name="cl-1060"></a>      this.groups().each(function(item) {
<a name="cl-1061"></a>        items.push(item.get('id'));
<a name="cl-1062"></a>      });
<a name="cl-1063"></a>      return items;
<a name="cl-1064"></a>    },
<a name="cl-1065"></a>
<a name="cl-1066"></a>    removeRole : function(roleid) {
<a name="cl-1067"></a>      var me = this;
<a name="cl-1068"></a>      //Remove group and refresh afterward
<a name="cl-1069"></a>      C8Y.client.user.removeRoleFromUser( this.get('id'), roleid, function() {
<a name="cl-1070"></a>        me.fireEvent('roleremove');
<a name="cl-1071"></a>        me.fireEvent('roleaction');
<a name="cl-1072"></a>      });
<a name="cl-1073"></a>    },
<a name="cl-1074"></a>
<a name="cl-1075"></a>    addRole : function(roleid) {
<a name="cl-1076"></a>      var me = this,
<a name="cl-1077"></a>          role = Ext.getStore('c8yuserrole').getById(roleid);
<a name="cl-1078"></a>      //Add Group and refresh afterward
<a name="cl-1079"></a>      C8Y.client.user.addRoleToUser(
<a name="cl-1080"></a>        this.get('id'), 
<a name="cl-1081"></a>        {role: { self: role.get('self') } },
<a name="cl-1082"></a>        function () {
<a name="cl-1083"></a>          me.fireEvent('roleadd');
<a name="cl-1084"></a>          me.fireEvent('roleaction');
<a name="cl-1085"></a>        }
<a name="cl-1086"></a>      );
<a name="cl-1087"></a>    },
<a name="cl-1088"></a>
<a name="cl-1089"></a>    updateRoles : function(selroles) {
<a name="cl-1090"></a>      var currentroles = this.getRolesIdArray(),
<a name="cl-1091"></a>        itemstodelete = [],
<a name="cl-1092"></a>        itemstoadd    = [],
<a name="cl-1093"></a>        me = this,
<a name="cl-1094"></a>        removecount = 0,
<a name="cl-1095"></a>        addcount = 0,
<a name="cl-1096"></a>        roleaction = 0,
<a name="cl-1097"></a>        callback = function() {
<a name="cl-1098"></a>          roleaction--;
<a name="cl-1099"></a>          if (!roleaction) {
<a name="cl-1100"></a>            me.un('roleaction', callback);
<a name="cl-1101"></a>            me.fireEvent('roleactioncomplete');
<a name="cl-1102"></a>            me.load();
<a name="cl-1103"></a>          }
<a name="cl-1104"></a>        };
<a name="cl-1105"></a>      
<a name="cl-1106"></a>      Ext.Array.each(selroles, function(item) {
<a name="cl-1107"></a>          if (!Ext.Array.contains(currentroles, item)) {
<a name="cl-1108"></a>            itemstoadd.push(item);
<a name="cl-1109"></a>          }
<a name="cl-1110"></a>      });
<a name="cl-1111"></a>
<a name="cl-1112"></a>      Ext.Array.each(currentroles, function(item) {
<a name="cl-1113"></a>          if (!Ext.Array.contains(selroles, item)) {
<a name="cl-1114"></a>            itemstodelete.push(item);
<a name="cl-1115"></a>          }
<a name="cl-1116"></a>      });
<a name="cl-1117"></a>      
<a name="cl-1118"></a>      Ext.Array.each(itemstodelete, function(item) {
<a name="cl-1119"></a>          removecount++;
<a name="cl-1120"></a>          roleaction++;
<a name="cl-1121"></a>          me.removeRole(item);
<a name="cl-1122"></a>      });
<a name="cl-1123"></a>
<a name="cl-1124"></a>      Ext.Array.each(itemstoadd, function(item) {
<a name="cl-1125"></a>          addcount++;
<a name="cl-1126"></a>          roleaction++;
<a name="cl-1127"></a>          me.addRole(item);
<a name="cl-1128"></a>      });
<a name="cl-1129"></a>
<a name="cl-1130"></a>      if (roleaction) {
<a name="cl-1131"></a>        me.on('roleaction', callback );             
<a name="cl-1132"></a>      }
<a name="cl-1133"></a>
<a name="cl-1134"></a>      return !!roleaction;
<a name="cl-1135"></a>    },
<a name="cl-1136"></a>
<a name="cl-1137"></a>    getRolesIdArray   : function() {
<a name="cl-1138"></a>      var items = [];
<a name="cl-1139"></a>      this.roles().each(function(item) {
<a name="cl-1140"></a>        items.push(item.get('id'));
<a name="cl-1141"></a>      });
<a name="cl-1142"></a>      return items;
<a name="cl-1143"></a>    }
<a name="cl-1144"></a>});
<a name="cl-1145"></a>   
<a name="cl-1146"></a>   
<a name="cl-1147"></a>/**
<a name="cl-1148"></a>* @class C8Y.store.User
<a name="cl-1149"></a>* Store Pre-configured with {@link C8Y.model.User}
<a name="cl-1150"></a>*/
<a name="cl-1151"></a>Ext.define('C8Y.store.User', {
<a name="cl-1152"></a>    extend  : 'Ext.data.Store',
<a name="cl-1153"></a>    model   : 'C8Y.model.User',
<a name="cl-1154"></a>    autoLoad : false,
<a name="cl-1155"></a>    pageSize: 1500,
<a name="cl-1156"></a>    requires : [
<a name="cl-1157"></a>        'C8Y.model.User'
<a name="cl-1158"></a>    ]
<a name="cl-1159"></a>});
<a name="cl-1160"></a>/**
<a name="cl-1161"></a>* @class C8Y.ux.InventoryGrid
<a name="cl-1162"></a>* Creates an InventoryGrid
<a name="cl-1163"></a>* @extends Ext.grid.Panel
<a name="cl-1164"></a>*/
<a name="cl-1165"></a>Ext.define('C8Y.ux.InventoryGrid',{
<a name="cl-1166"></a>    extend  : 'Ext.grid.Panel',
<a name="cl-1167"></a>    alias   : 'widget.c8yinventorygrid',
<a name="cl-1168"></a>    requires: [
<a name="cl-1169"></a>        'Ext.toolbar.Paging',
<a name="cl-1170"></a>        'C8Y.store.ManagedObject',
<a name="cl-1171"></a>        'C8Y.ux.PanelFeatures',
<a name="cl-1172"></a>        'Ext.grid.plugin.DragDrop',
<a name="cl-1173"></a>        'Ext.dd.DragZone',
<a name="cl-1174"></a>        'Ext.dd.DropZone',
<a name="cl-1175"></a>        'Ext.selection.RowModel',
<a name="cl-1176"></a>        'C8Y.ux.plugin.Panel'
<a name="cl-1177"></a>    ],
<a name="cl-1178"></a>
<a name="cl-1179"></a>    mixins  : {
<a name="cl-1180"></a>        feat    : 'C8Y.ux.PanelFeatures'
<a name="cl-1181"></a>    },
<a name="cl-1182"></a>    
<a name="cl-1183"></a>    plugins : [
<a name="cl-1184"></a>        { ptype: 'c8ypanel'}
<a name="cl-1185"></a>    ],
<a name="cl-1186"></a>
<a name="cl-1187"></a>    /**
<a name="cl-1188"></a>     * Automatic Method to initialize the grid, shouldn't be called by the developer
<a name="cl-1189"></a>     * @method
<a name="cl-1190"></a>     */
<a name="cl-1191"></a>    initComponent : function() {
<a name="cl-1192"></a>        var columns =  [
<a name="cl-1193"></a>            { dataIndex : 'id', width : 50, align:'center' },
<a name="cl-1194"></a>            { header    : 'Name',  dataIndex: 'name', flex: 1 },
<a name="cl-1195"></a>            { header    : 'Type',  dataIndex: 'type', flex: 1 }
<a name="cl-1196"></a>        ];
<a name="cl-1197"></a>        this.multiSelect = true;
<a name="cl-1198"></a>        this.columns = columns;
<a name="cl-1199"></a>        
<a name="cl-1200"></a>        if (this.managedObjectType) {
<a name="cl-1201"></a>            this.store =  Ext.create(
<a name="cl-1202"></a>                'C8Y.store.ManagedObject', 
<a name="cl-1203"></a>                { 
<a name="cl-1204"></a>                    autoLoad    : true, 
<a name="cl-1205"></a>                    proxy       : C8Y.client.inventory.getProxy(this.managedObjectType),
<a name="cl-1206"></a>                    storeId     : 'c8yinventorystore' 
<a name="cl-1207"></a>                }
<a name="cl-1208"></a>            ); 
<a name="cl-1209"></a>        } else {
<a name="cl-1210"></a>            this.store = Ext.create('C8Y.store.ManagedObject', {storeId : 'c8yinventorystore', autoLoad: true});
<a name="cl-1211"></a>        }
<a name="cl-1212"></a>        
<a name="cl-1213"></a>        this.dockedItems = [
<a name="cl-1214"></a>            {
<a name="cl-1215"></a>                xtype        : 'pagingtoolbar',
<a name="cl-1216"></a>                store        : this.store,
<a name="cl-1217"></a>                dock         : 'bottom'
<a name="cl-1218"></a>            },
<a name="cl-1219"></a>            this.buildTopActionMenu(this.buildTopMenuItems())
<a name="cl-1220"></a>        ];
<a name="cl-1221"></a>
<a name="cl-1222"></a>        if (!this.disableDrag) {
<a name="cl-1223"></a>            this.viewConfig = this.viewConfig || {};
<a name="cl-1224"></a>            this.viewConfig.listeners = this.viewConfig.listeners || {};
<a name="cl-1225"></a>            this.viewConfig.listeners.scope = this;
<a name="cl-1226"></a>            this.viewConfig.listeners.render = this.initializeDrop; 
<a name="cl-1227"></a>            this.on('render', this.initializeDrag, this);
<a name="cl-1228"></a>        }
<a name="cl-1229"></a>        
<a name="cl-1230"></a>        this.callParent(arguments);
<a name="cl-1231"></a>    },
<a name="cl-1232"></a>
<a name="cl-1233"></a>    initializeDrag : function() {
<a name="cl-1234"></a>        var me = this;
<a name="cl-1235"></a>        this.dragZone = Ext.create('Ext.dd.DragZone', this.getEl(), {
<a name="cl-1236"></a>      
<a name="cl-1237"></a>                getDragData: function(e) {
<a name="cl-1238"></a>                    var sourceEl = e.getTarget('.x-grid-row', 10), d;
<a name="cl-1239"></a>                    if (sourceEl) {
<a name="cl-1240"></a>                        var rec = me.getView().getRecord(sourceEl),
<a name="cl-1241"></a>                            id = Ext.id(),
<a name="cl-1242"></a>                            str = Ext.String.format('Add managed object {0} as a child asset', rec.get('id'));
<a name="cl-1243"></a>                            
<a name="cl-1244"></a>                        d = Ext.DomHelper.createDom({tag:'div', html: str, id: id});
<a name="cl-1245"></a>
<a name="cl-1246"></a>                        me.dragData = {
<a name="cl-1247"></a>                            sourceEl: sourceEl,
<a name="cl-1248"></a>                            repairXY: Ext.fly(sourceEl).getXY(),
<a name="cl-1249"></a>                            ddel : d,
<a name="cl-1250"></a>                            rec : rec
<a name="cl-1251"></a>                        };
<a name="cl-1252"></a>                        
<a name="cl-1253"></a>                        return me.dragData;
<a name="cl-1254"></a>                    }
<a name="cl-1255"></a>                },
<a name="cl-1256"></a>
<a name="cl-1257"></a>                getRepairXY: function() {
<a name="cl-1258"></a>                    return this.dragData.repairXY;
<a name="cl-1259"></a>                }
<a name="cl-1260"></a>        });
<a name="cl-1261"></a>    },
<a name="cl-1262"></a>
<a name="cl-1263"></a>    initializeDrop : function(v) {
<a name="cl-1264"></a>        var me = this;
<a name="cl-1265"></a>
<a name="cl-1266"></a>        this.dropZone = Ext.create('Ext.dd.DropZone', me.getEl(), {
<a name="cl-1267"></a>
<a name="cl-1268"></a>            getTargetFromEvent: function(e) {
<a name="cl-1269"></a>                return e.getTarget('.x-grid-row');
<a name="cl-1270"></a>            },
<a name="cl-1271"></a>
<a name="cl-1272"></a>            onNodeEnter : function(target, dd, e, data) {
<a name="cl-1273"></a>                Ext.fly(target).setStyle('background-color:green;');
<a name="cl-1274"></a>            },
<a name="cl-1275"></a>
<a name="cl-1276"></a>            onNodeOut : function(target, dd, e, data){
<a name="cl-1277"></a>                Ext.fly(target).setStyle('background-color:inherit;');
<a name="cl-1278"></a>            },
<a name="cl-1279"></a>
<a name="cl-1280"></a>            onNodeOver : function(target, dd, e, data){
<a name="cl-1281"></a>                var rec = data.rec,
<a name="cl-1282"></a>                    dropRec = me.getView().getRecord(target),
<a name="cl-1283"></a>                    dropAllowed = rec.get('id') != dropRec.get('id');
<a name="cl-1284"></a>                return (dropAllowed &amp;&amp; Ext.dd.DropZone.prototype.dropAllowed);
<a name="cl-1285"></a>            },
<a name="cl-1286"></a>
<a name="cl-1287"></a>            onNodeDrop : function(target, dd, e, data){
<a name="cl-1288"></a>                var rowBody = Ext.fly(target).findParent('.x-grid-row', null, false),
<a name="cl-1289"></a>                    rec = me.getView().getRecord(rowBody);
<a name="cl-1290"></a>                
<a name="cl-1291"></a>                rec.addAssets(data.rec);
<a name="cl-1292"></a>                return true;
<a name="cl-1293"></a>            }
<a name="cl-1294"></a>        });
<a name="cl-1295"></a>    },
<a name="cl-1296"></a>
<a name="cl-1297"></a>    buildTopMenuItems : function() {
<a name="cl-1298"></a>        var me = this;
<a name="cl-1299"></a>        return [
<a name="cl-1300"></a>                {
<a name="cl-1301"></a>                    text    : 'Add Managed Object',
<a name="cl-1302"></a>                    handler : this.onAddManagedObject
<a name="cl-1303"></a>                },
<a name="cl-1304"></a>                {
<a name="cl-1305"></a>                    text    : 'Actions',
<a name="cl-1306"></a>                    menu    : { 
<a name="cl-1307"></a>                        plain  : true,
<a name="cl-1308"></a>                        defaults : {'cls': 'action', width:200 }, 
<a name="cl-1309"></a>                        items : [
<a name="cl-1310"></a>                            { text : 'Remove Managed Object', scope: this, handler: this.onDeleteMo }
<a name="cl-1311"></a>                        ]
<a name="cl-1312"></a>                    }
<a name="cl-1313"></a>                }
<a name="cl-1314"></a>                // {
<a name="cl-1315"></a>                //     xtype   : 'textfield',
<a name="cl-1316"></a>                //     height  : 24,
<a name="cl-1317"></a>                //     emptyText : 'Search By Name',
<a name="cl-1318"></a>                //     cls     : '',
<a name="cl-1319"></a>                //     itemId  : 'searchField',
<a name="cl-1320"></a>                //     listeners: {
<a name="cl-1321"></a>                //         specialkey: function(field, e){
<a name="cl-1322"></a>                //             if (e.getKey() == e.ENTER) {
<a name="cl-1323"></a>                //                 me.searchUser(field.getValue());
<a name="cl-1324"></a>                //             }
<a name="cl-1325"></a>                //         }
<a name="cl-1326"></a>                //     }
<a name="cl-1327"></a>                // }
<a name="cl-1328"></a>        ];
<a name="cl-1329"></a>    },
<a name="cl-1330"></a>
<a name="cl-1331"></a>    onDeleteMo : function() {
<a name="cl-1332"></a>        var sel = this.getSelectionModel().getSelection(),
<a name="cl-1333"></a>             qt = sel.length,
<a name="cl-1334"></a>             me = this;
<a name="cl-1335"></a>         if (qt) {
<a name="cl-1336"></a>             Ext.Msg.confirm(
<a name="cl-1337"></a>                'Delete Managed Objects',
<a name="cl-1338"></a>                Ext.String.format('Do you want to delete {0} managed object{1}?', qt, (qt &gt; 1 ? 's':'')),
<a name="cl-1339"></a>                function(btn) {
<a name="cl-1340"></a>                    if (btn == 'yes') {
<a name="cl-1341"></a>                        Ext.Array.each(sel, function(record) {
<a name="cl-1342"></a>                            record.destroy();
<a name="cl-1343"></a>                        });
<a name="cl-1344"></a>                    }
<a name="cl-1345"></a>                }
<a name="cl-1346"></a>             );
<a name="cl-1347"></a>         }
<a name="cl-1348"></a>        
<a name="cl-1349"></a>    },
<a name="cl-1350"></a>
<a name="cl-1351"></a>    onAddManagedObject: function() {
<a name="cl-1352"></a>        this.fireEvent('createnew');
<a name="cl-1353"></a>    }
<a name="cl-1354"></a>});
<a name="cl-1355"></a>/**
<a name="cl-1356"></a>* @class C8Y.app.LoginWindow
<a name="cl-1357"></a>* Login window for the application
<a name="cl-1358"></a>* @extends Ext.form.Panel
<a name="cl-1359"></a>*/
<a name="cl-1360"></a>Ext.define('C8Y.app.LoginWindow', {
<a name="cl-1361"></a>    
<a name="cl-1362"></a>    extend      : 'Ext.form.Panel',
<a name="cl-1363"></a>    
<a name="cl-1364"></a>    requires    : [
<a name="cl-1365"></a>        'Ext.window.Window',
<a name="cl-1366"></a>        'Ext.form.field.Text',
<a name="cl-1367"></a>        'Ext.button.Button'
<a name="cl-1368"></a>    ],
<a name="cl-1369"></a>
<a name="cl-1370"></a>    initComponent : function() {
<a name="cl-1371"></a>        this.bodyCls = 'C8Yloginpanel';
<a name="cl-1372"></a>        this.bodyStyle = 'background-color:transparent;padding-left:100px;padding-top:80px';
<a name="cl-1373"></a>        this.style = 'background-color:transparent';
<a name="cl-1374"></a>        this.border = false;
<a name="cl-1375"></a>        this.items = this.buildItems();
<a name="cl-1376"></a>        this.addEvents(
<a name="cl-1377"></a>            /**
<a name="cl-1378"></a>             * @event
<a name="cl-1379"></a>             * Triggered after the login button pressed or enter button is pressed
<a name="cl-1380"></a>             * @param {String} login 
<a name="cl-1381"></a>             * @param {String} password 
<a name="cl-1382"></a>             */
<a name="cl-1383"></a>            'trylogin'
<a name="cl-1384"></a>        );
<a name="cl-1385"></a>        this.callParent(arguments);
<a name="cl-1386"></a>        this.openWindow();
<a name="cl-1387"></a>    },
<a name="cl-1388"></a>    
<a name="cl-1389"></a>    buildItems  : function() {
<a name="cl-1390"></a>		var me = this;
<a name="cl-1391"></a>        return [
<a name="cl-1392"></a>            {
<a name="cl-1393"></a>                xtype       : 'container',
<a name="cl-1394"></a>                height      : 50,
<a name="cl-1395"></a>                // html        : '&lt;h2 class="loginCumulocity"&gt;Cumulocity&lt;/h2&gt;&lt;small&gt;Version 1.0&lt;/small&gt;',
<a name="cl-1396"></a>                margin      : '0 0 30 0'
<a name="cl-1397"></a>            },
<a name="cl-1398"></a>			{
<a name="cl-1399"></a>                fieldLabel  : 'Tenant',
<a name="cl-1400"></a>                xtype       : 'textfield',
<a name="cl-1401"></a>                height      : 32,
<a name="cl-1402"></a>                width       : 350,
<a name="cl-1403"></a>                labelWidth  : 94,
<a name="cl-1404"></a>                required    : true,
<a name="cl-1405"></a>                name        : 'tenant'
<a name="cl-1406"></a>            },
<a name="cl-1407"></a>            {
<a name="cl-1408"></a>                fieldLabel  : 'Username',
<a name="cl-1409"></a>                xtype       : 'textfield',
<a name="cl-1410"></a>                height      : 32,
<a name="cl-1411"></a>                width       : 350,
<a name="cl-1412"></a>                labelWidth  : 94,
<a name="cl-1413"></a>                required    : true,
<a name="cl-1414"></a>                name        : 'login'
<a name="cl-1415"></a>            },
<a name="cl-1416"></a>            {
<a name="cl-1417"></a>                fieldLabel  : 'Password',
<a name="cl-1418"></a>                xtype       : 'textfield',
<a name="cl-1419"></a>                height      : 32,
<a name="cl-1420"></a>                width       : 350,
<a name="cl-1421"></a>                inputType   : 'password',
<a name="cl-1422"></a>                labelWidth  : 94,
<a name="cl-1423"></a>                required    : true,
<a name="cl-1424"></a>                name        : 'password',
<a name="cl-1425"></a>				listeners: {
<a name="cl-1426"></a>	                specialkey: function(field, e){
<a name="cl-1427"></a>	                    if (e.getKey() == e.ENTER) {
<a name="cl-1428"></a>	                        me.onLoginClick();
<a name="cl-1429"></a>	                    }
<a name="cl-1430"></a>	                }
<a name="cl-1431"></a>	            }
<a name="cl-1432"></a>            },
<a name="cl-1433"></a>            {   
<a name="cl-1434"></a>                xtype       : 'button',
<a name="cl-1435"></a>                text        : 'Login',
<a name="cl-1436"></a>                scale       : 'medium',
<a name="cl-1437"></a>                width       : 120,
<a name="cl-1438"></a>                margin      : '20 0 0 99',
<a name="cl-1439"></a>                style       : 'font-size:18px',
<a name="cl-1440"></a>                handler     : this.onLoginClick,
<a name="cl-1441"></a>                scope       : this,
<a name="cl-1442"></a>                cls         : 'mainAction'
<a name="cl-1443"></a>            }
<a name="cl-1444"></a>        ];
<a name="cl-1445"></a>    },
<a name="cl-1446"></a>    
<a name="cl-1447"></a>    onLoginClick : function() {
<a name="cl-1448"></a>        var data = this.getForm().getValues(),
<a name="cl-1449"></a>			btn = this.query('button').shift(),
<a name="cl-1450"></a>            that = this;
<a name="cl-1451"></a>
<a name="cl-1452"></a>        btn.setText('Loggin in...');
<a name="cl-1453"></a>		btn.disable();
<a name="cl-1454"></a>        that.fireEvent('trylogin', data.login, data.password, data.tenant);
<a name="cl-1455"></a>    },
<a name="cl-1456"></a>    
<a name="cl-1457"></a>    //Wrap the panel in a window component and show it
<a name="cl-1458"></a>    openWindow : function() {
<a name="cl-1459"></a>        var win = Ext.create('Ext.window.Window', {
<a name="cl-1460"></a>            style       : 'border:1px solid #DDD;-moz-border-radius: 0px;-webkit-border-radius: 0px;border-radius: 0px;',
<a name="cl-1461"></a>            width       : 714,
<a name="cl-1462"></a>            height      : 390,
<a name="cl-1463"></a>            items       : this,
<a name="cl-1464"></a>            header      : false,
<a name="cl-1465"></a>            draggable   : false,
<a name="cl-1466"></a>            closable    : false,
<a name="cl-1467"></a>            resizable   : false,
<a name="cl-1468"></a>            border      : false,
<a name="cl-1469"></a>            layout      : 'fit',
<a name="cl-1470"></a>            cls         : 'C8Yloginwindow',
<a name="cl-1471"></a>            bodyStyle   : 'background:transparent'
<a name="cl-1472"></a>        });
<a name="cl-1473"></a>
<a name="cl-1474"></a>        win.show();
<a name="cl-1475"></a>    },
<a name="cl-1476"></a>    
<a name="cl-1477"></a>    /**
<a name="cl-1478"></a>    * @returns null
<a name="cl-1479"></a>    * 
<a name="cl-1480"></a>    */
<a name="cl-1481"></a>    closeWindow : function() {
<a name="cl-1482"></a>		this.ownerCt.destroy();
<a name="cl-1483"></a>    },
<a name="cl-1484"></a>
<a name="cl-1485"></a>	resetBtn : function() {
<a name="cl-1486"></a>		var btn = this.query('button').shift();
<a name="cl-1487"></a>		btn.setText('Login');
<a name="cl-1488"></a>		btn.enable();
<a name="cl-1489"></a>	}
<a name="cl-1490"></a>});
<a name="cl-1491"></a>/**
<a name="cl-1492"></a>* @class C8Y.ux.ManagedObjectForm
<a name="cl-1493"></a>* Form for Managed Objects
<a name="cl-1494"></a>* @extends Ext.form.Panel
<a name="cl-1495"></a>* @alias c8ymanagedobjectform
<a name="cl-1496"></a>*/
<a name="cl-1497"></a>Ext.define('C8Y.ux.ManagedObjectForm', {
<a name="cl-1498"></a>    
<a name="cl-1499"></a>    extend : 'Ext.form.Panel',
<a name="cl-1500"></a>    
<a name="cl-1501"></a>    requires : [
<a name="cl-1502"></a>        'Ext.form.field.Text',
<a name="cl-1503"></a>        'Ext.form.field.Hidden',
<a name="cl-1504"></a>        'Ext.form.field.Display',
<a name="cl-1505"></a>        'Ext.form.field.ComboBox',
<a name="cl-1506"></a>        'Ext.layout.container.Anchor',
<a name="cl-1507"></a>        'C8Y.model.ManagedObject',
<a name="cl-1508"></a>        'C8Y.ux.PanelFeatures'
<a name="cl-1509"></a>    ],
<a name="cl-1510"></a>
<a name="cl-1511"></a>    mixins  : {
<a name="cl-1512"></a>        feat    : 'C8Y.ux.PanelFeatures'
<a name="cl-1513"></a>    },
<a name="cl-1514"></a>    
<a name="cl-1515"></a>    alias   : 'widget.c8ymanagedobjectform',
<a name="cl-1516"></a>    
<a name="cl-1517"></a>    initComponent : function() {
<a name="cl-1518"></a>        this.dockedItems = [
<a name="cl-1519"></a>            {
<a name="cl-1520"></a>                xtype   : 'toolbar',
<a name="cl-1521"></a>                dock    : 'bottom',
<a name="cl-1522"></a>                ui      : 'footer',
<a name="cl-1523"></a>                items   : this.buildButtons()
<a name="cl-1524"></a>            },
<a name="cl-1525"></a>            this.buildTopActionMenu(this.buildTopMenuItems())
<a name="cl-1526"></a>        ];
<a name="cl-1527"></a>        this.bodyPadding = 10;
<a name="cl-1528"></a>        this.defaultType = 'textfield';
<a name="cl-1529"></a>        this.fieldDefaults = {
<a name="cl-1530"></a>            labelAlign: 'top'
<a name="cl-1531"></a>        };
<a name="cl-1532"></a>        this.autoScroll = true;
<a name="cl-1533"></a>        this.labelStyle = 'font-size:13px;font-weight:bold;color:#333;font-family:arial;';
<a name="cl-1534"></a>        this.callParent();
<a name="cl-1535"></a>    },
<a name="cl-1536"></a>    propsplitter : "::|::",
<a name="cl-1537"></a>    ignores : [
<a name="cl-1538"></a>        'childAssets',
<a name="cl-1539"></a>        'childDevices',
<a name="cl-1540"></a>        'parents',
<a name="cl-1541"></a>        'attrs',
<a name="cl-1542"></a>        'self'
<a name="cl-1543"></a>    ],
<a name="cl-1544"></a>
<a name="cl-1545"></a>    readOnly : [
<a name="cl-1546"></a>        'id',
<a name="cl-1547"></a>        'lastUpdated'
<a name="cl-1548"></a>    ],
<a name="cl-1549"></a>
<a name="cl-1550"></a>    notClearable : [
<a name="cl-1551"></a>        'name',
<a name="cl-1552"></a>        'type'
<a name="cl-1553"></a>    ],
<a name="cl-1554"></a>    
<a name="cl-1555"></a>    buildTopMenuItems : function() {
<a name="cl-1556"></a>        return [
<a name="cl-1557"></a>            {
<a name="cl-1558"></a>                text    : 'Add Property',
<a name="cl-1559"></a>                handler : this.onAddProperty,
<a name="cl-1560"></a>                scope   : this,
<a name="cl-1561"></a>                scale   : 'medium',
<a name="cl-1562"></a>                itemId  : 'addpropertybutton'
<a name="cl-1563"></a>            }
<a name="cl-1564"></a>        ];
<a name="cl-1565"></a>    },
<a name="cl-1566"></a>    
<a name="cl-1567"></a>    buildButtons : function() {
<a name="cl-1568"></a>       return  [
<a name="cl-1569"></a>            {
<a name="cl-1570"></a>                text    : 'Save',
<a name="cl-1571"></a>                handler : this.onSave,
<a name="cl-1572"></a>                scope   : this,
<a name="cl-1573"></a>                scale   : 'medium',
<a name="cl-1574"></a>                cls     : 'mainAction'
<a name="cl-1575"></a>            },
<a name="cl-1576"></a>            {
<a name="cl-1577"></a>                text    : 'Save as a copy',
<a name="cl-1578"></a>                handler : this.onSaveAs,
<a name="cl-1579"></a>                scope   : this,
<a name="cl-1580"></a>                scale   : 'medium',
<a name="cl-1581"></a>                cls     : 'mainAction'
<a name="cl-1582"></a>            },
<a name="cl-1583"></a>            {
<a name="cl-1584"></a>                text    : 'Cancel Changes',
<a name="cl-1585"></a>                handler : this.onRevert,
<a name="cl-1586"></a>                scope   : this,
<a name="cl-1587"></a>                scale   : 'medium',
<a name="cl-1588"></a>                cls     : 'mainAction'
<a name="cl-1589"></a>            }
<a name="cl-1590"></a>       ];
<a name="cl-1591"></a>    },
<a name="cl-1592"></a>
<a name="cl-1593"></a>    buildFields : function(data, parentkey) {
<a name="cl-1594"></a>        var keys = Ext.Object.getKeys(data),
<a name="cl-1595"></a>            items = [],
<a name="cl-1596"></a>            me = this;
<a name="cl-1597"></a>        
<a name="cl-1598"></a>        if (parentkey &amp;&amp; !keys.length) {
<a name="cl-1599"></a>            key = parentkey + me.propsplitter;
<a name="cl-1600"></a>            items.push({
<a name="cl-1601"></a>                name    : parentkey + me.propsplitter,
<a name="cl-1602"></a>                xtype   : 'hidden',
<a name="cl-1603"></a>                value   : 0
<a name="cl-1604"></a>            });
<a name="cl-1605"></a>        }
<a name="cl-1606"></a>        
<a name="cl-1607"></a>        
<a name="cl-1608"></a>        
<a name="cl-1609"></a>        Ext.Array.each(keys, function(key) {
<a name="cl-1610"></a>            var val = data[key],
<a name="cl-1611"></a>                localkey,
<a name="cl-1612"></a>                item;
<a name="cl-1613"></a>            
<a name="cl-1614"></a>            if (Ext.Array.contains(me.ignores, key)) {
<a name="cl-1615"></a>                return;
<a name="cl-1616"></a>            }
<a name="cl-1617"></a>            
<a name="cl-1618"></a>            if (parentkey) {
<a name="cl-1619"></a>                localkey = key;
<a name="cl-1620"></a>                key = parentkey + me.propsplitter + key;
<a name="cl-1621"></a>            }
<a name="cl-1622"></a>             
<a name="cl-1623"></a>            if (Ext.isObject(val)) {
<a name="cl-1624"></a>                item = {
<a name="cl-1625"></a>                    xtype       : 'fieldcontainer',
<a name="cl-1626"></a>                    fieldLabel  : localkey || key,
<a name="cl-1627"></a>                    name        : key,
<a name="cl-1628"></a>                    fieldBodyCls: 'c8yinnerField',
<a name="cl-1629"></a>                    items       : me.buildFields(val, key),
<a name="cl-1630"></a>                    defaults    : {
<a name="cl-1631"></a>                        labelAlign : 'top'
<a name="cl-1632"></a>                    },
<a name="cl-1633"></a>                    labelStyle  : me.labelStyle,
<a name="cl-1634"></a>                    width   : 300,
<a name="cl-1635"></a>                    margin  : '0 0 10 0'
<a name="cl-1636"></a>                };
<a name="cl-1637"></a>            } else {
<a name="cl-1638"></a>                var type = Ext.Array.contains(me.readOnly, key) ? 'displayfield' : (Ext.Array.contains(me.notClearable, key) ? 'textfield' : 'c8yclearablefield');
<a name="cl-1639"></a>                item = {
<a name="cl-1640"></a>                    value       : val,
<a name="cl-1641"></a>                    fieldLabel  : localkey || key,
<a name="cl-1642"></a>                    name        : key,
<a name="cl-1643"></a>                    xtype       : type,
<a name="cl-1644"></a>                    width       : 300,
<a name="cl-1645"></a>                    labelStyle  : me.labelStyle,
<a name="cl-1646"></a>                    margin      : '0 0 10 0'
<a name="cl-1647"></a>                };
<a name="cl-1648"></a>            }
<a name="cl-1649"></a>            items.push(item);
<a name="cl-1650"></a>        });
<a name="cl-1651"></a>        
<a name="cl-1652"></a>        return  items;
<a name="cl-1653"></a>    },
<a name="cl-1654"></a>
<a name="cl-1655"></a>    buildDynamicFormItems : function(data) {
<a name="cl-1656"></a>        var items = this.buildFields(data);
<a name="cl-1657"></a>        this.add(items);
<a name="cl-1658"></a>        this.el.unmask();
<a name="cl-1659"></a>    },
<a name="cl-1660"></a>
<a name="cl-1661"></a>    getValues : function() {
<a name="cl-1662"></a>        var rawValues = this.getForm().getValues(),
<a name="cl-1663"></a>            s = this.propsplitter;
<a name="cl-1664"></a>        Ext.Object.each(rawValues, function(key, value, myself) {
<a name="cl-1665"></a>            var props,
<a name="cl-1666"></a>                step = null;
<a name="cl-1667"></a>            if (Ext.isString(value) &amp;&amp; value.match(':::null:::')) {
<a name="cl-1668"></a>                value = null;
<a name="cl-1669"></a>                rawValues[key] = null;
<a name="cl-1670"></a>            }
<a name="cl-1671"></a>            if (key.match(s)) {
<a name="cl-1672"></a>                props = key.split(s);
<a name="cl-1673"></a>                while(props.length) {
<a name="cl-1674"></a>                    var p = props.shift();
<a name="cl-1675"></a>                    if (!p) break;
<a name="cl-1676"></a>                    step = step || rawValues;
<a name="cl-1677"></a>                    step = step[p] = (props.length ? (step[p] || {}) : value);
<a name="cl-1678"></a>                }
<a name="cl-1679"></a>                delete rawValues[key];
<a name="cl-1680"></a>            }
<a name="cl-1681"></a>        });
<a name="cl-1682"></a>        return rawValues;   
<a name="cl-1683"></a>    },
<a name="cl-1684"></a>    
<a name="cl-1685"></a>    /**
<a name="cl-1686"></a>    * Loads a Managed Object
<a name="cl-1687"></a>    * @param mo {C8Y.model.ManagedObject/Number} A managed object or numeric id
<a name="cl-1688"></a>    */
<a name="cl-1689"></a>    loadManagedObject : function(mo) {
<a name="cl-1690"></a>        var id = Ext.isNumeric(mo) ? mo : mo.get('id'),
<a name="cl-1691"></a>            me = this,
<a name="cl-1692"></a>            rec;
<a name="cl-1693"></a>        
<a name="cl-1694"></a>        this.removeAll();
<a name="cl-1695"></a>        if (id) {
<a name="cl-1696"></a>            this.el.mask('Loading');
<a name="cl-1697"></a>            C8Y.client.inventory.get(id, function(res) {
<a name="cl-1698"></a>                me.el.unmask();
<a name="cl-1699"></a>                me.buildDynamicFormItems(res);
<a name="cl-1700"></a>            });    
<a name="cl-1701"></a>        } else {
<a name="cl-1702"></a>            me.buildDynamicFormItems(mo.data);
<a name="cl-1703"></a>        }
<a name="cl-1704"></a>        
<a name="cl-1705"></a>        this.record = mo;
<a name="cl-1706"></a>    },
<a name="cl-1707"></a>
<a name="cl-1708"></a>    reloadManagedObject : function() {
<a name="cl-1709"></a>        this.loadManagedObject(this.record);
<a name="cl-1710"></a>    },
<a name="cl-1711"></a>    
<a name="cl-1712"></a>    getRecord : function() {
<a name="cl-1713"></a>        return this.record || this.callParent();
<a name="cl-1714"></a>    },
<a name="cl-1715"></a>    
<a name="cl-1716"></a>    /**
<a name="cl-1717"></a>    * Create a new record to be saved
<a name="cl-1718"></a>    */
<a name="cl-1719"></a>    createNewRecord : function(data) {
<a name="cl-1720"></a>        data = data || {};
<a name="cl-1721"></a>        var newRecord = Ext.create('C8Y.model.ManagedObject', data);
<a name="cl-1722"></a>        this.loadManagedObject(newRecord);
<a name="cl-1723"></a>        return newRecord;
<a name="cl-1724"></a>    },
<a name="cl-1725"></a>    
<a name="cl-1726"></a>    addNewField : function(vals) {
<a name="cl-1727"></a>        var name = vals.name,
<a name="cl-1728"></a>            type = vals.type,
<a name="cl-1729"></a>            parent = vals.parent,
<a name="cl-1730"></a>            item = {
<a name="cl-1731"></a>                xtype       : type,
<a name="cl-1732"></a>                fieldLabel  : name,
<a name="cl-1733"></a>                name        : name,
<a name="cl-1734"></a>                width       : 300,
<a name="cl-1735"></a>                labelStyle  : this.labelStyle,
<a name="cl-1736"></a>                margin      : '0 0 10 0'
<a name="cl-1737"></a>            },
<a name="cl-1738"></a>            root;
<a name="cl-1739"></a>                  
<a name="cl-1740"></a>        if (type == 'fieldcontainer') {
<a name="cl-1741"></a>            item.fieldBodyCls = 'c8yinnerField';
<a name="cl-1742"></a>            item.items = [
<a name="cl-1743"></a>                {
<a name="cl-1744"></a>                    name    : name + this.propsplitter,
<a name="cl-1745"></a>                    width   : 300,
<a name="cl-1746"></a>                    xtype   : 'hidden',
<a name="cl-1747"></a>                    value   : 0
<a name="cl-1748"></a>                }
<a name="cl-1749"></a>            ];
<a name="cl-1750"></a>        }
<a name="cl-1751"></a>        
<a name="cl-1752"></a>        if (parent) {
<a name="cl-1753"></a>            root = this.query('#'+parent).shift();
<a name="cl-1754"></a>            item.name = root.name + this.propsplitter + item.name;
<a name="cl-1755"></a>        } else {
<a name="cl-1756"></a>            root = this;
<a name="cl-1757"></a>        }
<a name="cl-1758"></a>        
<a name="cl-1759"></a>        root.add(item);
<a name="cl-1760"></a>    },
<a name="cl-1761"></a>    
<a name="cl-1762"></a>    openAddPropertyWindow : function() {
<a name="cl-1763"></a>        var parentFields = [],
<a name="cl-1764"></a>            items = [
<a name="cl-1765"></a>                {
<a name="cl-1766"></a>                    fieldLabel : 'Name',
<a name="cl-1767"></a>                    name       : 'name',
<a name="cl-1768"></a>                    xtype      : 'textfield'
<a name="cl-1769"></a>                },
<a name="cl-1770"></a>                {
<a name="cl-1771"></a>                    fieldLabel  : 'Type',
<a name="cl-1772"></a>                    xtype       : 'combo',
<a name="cl-1773"></a>                    queryMode   : 'local',
<a name="cl-1774"></a>                    name        : 'type',
<a name="cl-1775"></a>                    displayField: 'name',
<a name="cl-1776"></a>                    valueField  : 'typ',
<a name="cl-1777"></a>                    store       : Ext.create('Ext.data.Store', {
<a name="cl-1778"></a>                        fields  : ['typ', 'name'],
<a name="cl-1779"></a>                        data    : [ { typ: 'textfield', name: 'Simple' }, { typ: 'fieldcontainer', name: 'Complex' }]
<a name="cl-1780"></a>                    })
<a name="cl-1781"></a>                }
<a name="cl-1782"></a>            ],
<a name="cl-1783"></a>            win,
<a name="cl-1784"></a>            position = this.query('#addpropertybutton').shift().el.getXY();
<a name="cl-1785"></a>            
<a name="cl-1786"></a>        parentFields = this.query('fieldcontainer').map(function(item) {
<a name="cl-1787"></a>            return {
<a name="cl-1788"></a>                itemId  : item.getId(),
<a name="cl-1789"></a>                name    : item.fieldLabel
<a name="cl-1790"></a>            };
<a name="cl-1791"></a>        });
<a name="cl-1792"></a>
<a name="cl-1793"></a>        if (parentFields.length) {
<a name="cl-1794"></a>            items.push({
<a name="cl-1795"></a>                fieldLabel  : 'Parent',
<a name="cl-1796"></a>                xtype       : 'combo',
<a name="cl-1797"></a>                queryMode   : 'local',
<a name="cl-1798"></a>                name        : 'parent',
<a name="cl-1799"></a>                displayField: 'name',
<a name="cl-1800"></a>                valueField  : 'itemId',
<a name="cl-1801"></a>                store       : Ext.create('Ext.data.Store', {
<a name="cl-1802"></a>                    fields  : ['itemId', 'name'],
<a name="cl-1803"></a>                    data    : parentFields
<a name="cl-1804"></a>                })
<a name="cl-1805"></a>            });
<a name="cl-1806"></a>        }
<a name="cl-1807"></a>        
<a name="cl-1808"></a>        
<a name="cl-1809"></a>        
<a name="cl-1810"></a>        win = Ext.create('Ext.window.Window', {
<a name="cl-1811"></a>            width : 400,
<a name="cl-1812"></a>            autoHeight : true,
<a name="cl-1813"></a>            resizable : false,
<a name="cl-1814"></a>            closable : false,
<a name="cl-1815"></a>            preventHeader : true,
<a name="cl-1816"></a>            x   : position[0] + 90,
<a name="cl-1817"></a>            y   : position[1],
<a name="cl-1818"></a>            items   : {
<a name="cl-1819"></a>                xtype    : 'form',
<a name="cl-1820"></a>                padding  : 10,
<a name="cl-1821"></a>                border   : false,
<a name="cl-1822"></a>                style    : 'background:#FFF',
<a name="cl-1823"></a>                layout   : 'anchor',
<a name="cl-1824"></a>                fieldDefaults : {anchor: '100%'},
<a name="cl-1825"></a>                items    : items
<a name="cl-1826"></a>            },
<a name="cl-1827"></a>            buttons : [
<a name="cl-1828"></a>                {
<a name="cl-1829"></a>                    text : 'Add',
<a name="cl-1830"></a>                    scope : this,
<a name="cl-1831"></a>                    handler : function() {
<a name="cl-1832"></a>                        var form = win.child('form'),
<a name="cl-1833"></a>                            values = form.getForm().getValues();
<a name="cl-1834"></a>                        this.addNewField(values);
<a name="cl-1835"></a>                        win.destroy();
<a name="cl-1836"></a>                    }
<a name="cl-1837"></a>                },
<a name="cl-1838"></a>                {
<a name="cl-1839"></a>                    text : 'Cancel',
<a name="cl-1840"></a>                    scope : this,
<a name="cl-1841"></a>                    handler : function() {
<a name="cl-1842"></a>                        win.destroy();
<a name="cl-1843"></a>                    }
<a name="cl-1844"></a>                }
<a name="cl-1845"></a>            ]
<a name="cl-1846"></a>        });
<a name="cl-1847"></a>        win.show();
<a name="cl-1848"></a>    },
<a name="cl-1849"></a>    
<a name="cl-1850"></a>    onSave : function() {
<a name="cl-1851"></a>        var me = this,
<a name="cl-1852"></a>            values = this.getValues(),
<a name="cl-1853"></a>            record = me.getRecord(),
<a name="cl-1854"></a>            id = record.get('id');
<a name="cl-1855"></a>            
<a name="cl-1856"></a>        me.el.mask('Saving');
<a name="cl-1857"></a>        delete values.id;
<a name="cl-1858"></a>        record.set(values);
<a name="cl-1859"></a>        record.save(function() {
<a name="cl-1860"></a>            me.loadManagedObject(record);
<a name="cl-1861"></a>            me.el.unmask();
<a name="cl-1862"></a>        }, values);
<a name="cl-1863"></a>    },
<a name="cl-1864"></a>
<a name="cl-1865"></a>    onSaveAs : function() {
<a name="cl-1866"></a>        var me = this,
<a name="cl-1867"></a>            values = this.getValues(),
<a name="cl-1868"></a>            record;
<a name="cl-1869"></a>        record = this.createNewRecord();
<a name="cl-1870"></a>        me.el.mask('Saving');
<a name="cl-1871"></a>        delete values.id;
<a name="cl-1872"></a>        record.set(values);
<a name="cl-1873"></a>        console.dir(values);
<a name="cl-1874"></a>        record.save(function() {
<a name="cl-1875"></a>            me.loadManagedObject(record);
<a name="cl-1876"></a>            me.el.unmask();
<a name="cl-1877"></a>        }, values);
<a name="cl-1878"></a>    },
<a name="cl-1879"></a>    
<a name="cl-1880"></a>    onRevert : function() {
<a name="cl-1881"></a>        var isNew = !this.record.get('id');
<a name="cl-1882"></a>        if (isNew) {
<a name="cl-1883"></a>            this.createNewRecord();
<a name="cl-1884"></a>        } else {
<a name="cl-1885"></a>            this.reloadManagedObject();
<a name="cl-1886"></a>        }
<a name="cl-1887"></a>    },
<a name="cl-1888"></a>    
<a name="cl-1889"></a>    onCreateNew : function() {
<a name="cl-1890"></a>        this.createNewRecord();
<a name="cl-1891"></a>    },
<a name="cl-1892"></a>
<a name="cl-1893"></a>    onAddProperty : function() {
<a name="cl-1894"></a>        this.openAddPropertyWindow();
<a name="cl-1895"></a>    }
<a name="cl-1896"></a>});
<a name="cl-1897"></a>
<a name="cl-1898"></a>Ext.define('C8Y.ux.ClearableField', {
<a name="cl-1899"></a>    extend: 'Ext.form.field.Trigger',
<a name="cl-1900"></a>    alias: 'widget.c8yclearablefield',
<a name="cl-1901"></a>    triggerCls : 'x-form-clear-trigger',
<a name="cl-1902"></a>    getRawValue : function() {
<a name="cl-1903"></a>        return (this.toclear ? ':::null:::' : this.callParent());
<a name="cl-1904"></a>    },
<a name="cl-1905"></a>    onTriggerClick: function() {
<a name="cl-1906"></a>        var clear = this.toclear = !this.toclear,
<a name="cl-1907"></a>            stl = clear ? 'text-decoration: line-through;color:red;font-style:italic;' : 'text-decoration: none;color:#000;font-style:normal;';
<a name="cl-1908"></a>        this.setEditable(!clear);
<a name="cl-1909"></a>        this.setFieldStyle(stl);
<a name="cl-1910"></a>    }
<a name="cl-1911"></a>});
<a name="cl-1912"></a>Ext.define('C8Y.ux.DeviceControlPanel', {
<a name="cl-1913"></a>    extend  : 'Ext.form.Panel',
<a name="cl-1914"></a>    alias   : 'widget.c8ydevicecontrolpanel',
<a name="cl-1915"></a>    requires: [
<a name="cl-1916"></a>        'Ext.form.field.Time'
<a name="cl-1917"></a>    ],
<a name="cl-1918"></a>    
<a name="cl-1919"></a>    initComponent : function() {
<a name="cl-1920"></a>        this.dockedItems = this.buildDockedItems();
<a name="cl-1921"></a>        this.controlTypes = this.buildControlTypes();
<a name="cl-1922"></a>        this.bodyStyle = "padding:10px;border:none;";
<a name="cl-1923"></a>        this.callParent();
<a name="cl-1924"></a>    },
<a name="cl-1925"></a>    
<a name="cl-1926"></a>    buildDockedItems : function() {
<a name="cl-1927"></a>        var dock = this.dockedItems || [];
<a name="cl-1928"></a>        if (!Ext.isArray(dock)) {
<a name="cl-1929"></a>            dock = [dock];
<a name="cl-1930"></a>        }
<a name="cl-1931"></a>        
<a name="cl-1932"></a>        dock.push({
<a name="cl-1933"></a>            xtype   : 'container',
<a name="cl-1934"></a>            itemId  : 'sideBar',
<a name="cl-1935"></a>            dock    : 'left',
<a name="cl-1936"></a>            width   : 150,
<a name="cl-1937"></a>            padding : 10,
<a name="cl-1938"></a>            defaults: {
<a name="cl-1939"></a>                margin : '0 0 3px 0'
<a name="cl-1940"></a>            },
<a name="cl-1941"></a>            layout  : {
<a name="cl-1942"></a>                type    : 'vbox',
<a name="cl-1943"></a>                align   : 'stretch'
<a name="cl-1944"></a>            }
<a name="cl-1945"></a>        });
<a name="cl-1946"></a>        dock.push({
<a name="cl-1947"></a>            xtype   : 'container',
<a name="cl-1948"></a>            dock    : 'right',
<a name="cl-1949"></a>            padding : 10,
<a name="cl-1950"></a>            width   : 150,
<a name="cl-1951"></a>            layout  : {
<a name="cl-1952"></a>                type    : 'hbox',
<a name="cl-1953"></a>                align   : 'center'
<a name="cl-1954"></a>            },
<a name="cl-1955"></a>            items   : {
<a name="cl-1956"></a>                xtype   : 'button',
<a name="cl-1957"></a>                cls     : 'action',
<a name="cl-1958"></a>                dock    : 'right',
<a name="cl-1959"></a>                scale   : 'medium',
<a name="cl-1960"></a>                text    : 'Submit Operation',
<a name="cl-1961"></a>                flex    : 1,
<a name="cl-1962"></a>                scope   : this,
<a name="cl-1963"></a>                handler : this.onSubmitOperation
<a name="cl-1964"></a>            }
<a name="cl-1965"></a>        });
<a name="cl-1966"></a>        
<a name="cl-1967"></a>        return dock;
<a name="cl-1968"></a>    },
<a name="cl-1969"></a>    
<a name="cl-1970"></a>    setManagedObject : function(mo) {
<a name="cl-1971"></a>        var me = this,
<a name="cl-1972"></a>            keys;
<a name="cl-1973"></a>        this.managedObject = mo;
<a name="cl-1974"></a>        this.setTitle('Device Control - ' + mo.get('name') + " (Type: " + mo.get('type')+ ")");
<a name="cl-1975"></a>        C8Y.client.inventory.get(mo.get('id'), function(r) {
<a name="cl-1976"></a>            keys = Ext.Object.getKeys(r);
<a name="cl-1977"></a>            me.parseKeys(keys);
<a name="cl-1978"></a>        });
<a name="cl-1979"></a>    },
<a name="cl-1980"></a>    
<a name="cl-1981"></a>    parseKeys : function(keys) {
<a name="cl-1982"></a>        var ctypes = this.controlTypesList,
<a name="cl-1983"></a>            lbar = this.getComponent('sideBar'),
<a name="cl-1984"></a>            items = [],
<a name="cl-1985"></a>            me = this,
<a name="cl-1986"></a>            id = Ext.id();
<a name="cl-1987"></a>        
<a name="cl-1988"></a>        lbar.removeAll();
<a name="cl-1989"></a>        this.removeAll();
<a name="cl-1990"></a>        Ext.Array.each(keys, function(key) {
<a name="cl-1991"></a>            var ctype = me.controlTypesMap[key];
<a name="cl-1992"></a>            if (Ext.Array.contains(ctypes, key)) {
<a name="cl-1993"></a>                items.push({
<a name="cl-1994"></a>                    xtype   : 'button',
<a name="cl-1995"></a>                    toggleGroup: 'availablecontrols' +id,
<a name="cl-1996"></a>                    text    : ctype.name,
<a name="cl-1997"></a>                    ctypeId : ctype.id,
<a name="cl-1998"></a>                    scope   : me,
<a name="cl-1999"></a>                    handler : me.onTypePress
<a name="cl-2000"></a>                });
<a name="cl-2001"></a>            }
<a name="cl-2002"></a>        });
<a name="cl-2003"></a>        lbar.add(items);
<a name="cl-2004"></a>    },
<a name="cl-2005"></a>    
<a name="cl-2006"></a>    onTypePress : function(btn) {
<a name="cl-2007"></a>        var ctype = this.controlTypesMap[btn.ctypeId];
<a name="cl-2008"></a>        this.controlTypeCurrent = btn.ctypeId;
<a name="cl-2009"></a>        this.removeAll();
<a name="cl-2010"></a>        this.add(ctype.form);
<a name="cl-2011"></a>    },
<a name="cl-2012"></a>    
<a name="cl-2013"></a>    onSubmitOperation : function() {
<a name="cl-2014"></a>        var vals = this.getForm().getValues(),
<a name="cl-2015"></a>            data = {};
<a name="cl-2016"></a>        
<a name="cl-2017"></a>        data[this.controlTypeCurrent] = vals;
<a name="cl-2018"></a>        C8Y.client.devicecontrol.create(this.managedObject.get('id'), data, function(r) {
<a name="cl-2019"></a>            Ext.window.MessageBox.alert('Operation Created');
<a name="cl-2020"></a>        });
<a name="cl-2021"></a>    },
<a name="cl-2022"></a>    
<a name="cl-2023"></a>    buildControlTypes : function() {
<a name="cl-2024"></a>        var ctypes = this.controlTypes || [],
<a name="cl-2025"></a>            ctypesMap = this.controlTypesMap = {};
<a name="cl-2026"></a>            builtIn = [];
<a name="cl-2027"></a>        if (!Ext.isArray(ctypes)) {
<a name="cl-2028"></a>            dock = [dock];
<a name="cl-2029"></a>        }
<a name="cl-2030"></a>        
<a name="cl-2031"></a>        builtIn = [
<a name="cl-2032"></a>            {
<a name="cl-2033"></a>                id  : 'com_cumulocity_model_control_Relay',
<a name="cl-2034"></a>                name: 'Relay',
<a name="cl-2035"></a>                form: [
<a name="cl-2036"></a>                    {
<a name="cl-2037"></a>                        xtype    : 'combo',
<a name="cl-2038"></a>                        store    : Ext.create('Ext.data.Store', {
<a name="cl-2039"></a>                            fields: ['state'],
<a name="cl-2040"></a>                            data : [
<a name="cl-2041"></a>                                {"state":"OPEN"},
<a name="cl-2042"></a>                                {"state":"CLOSE"}
<a name="cl-2043"></a>                            ]
<a name="cl-2044"></a>                        }),
<a name="cl-2045"></a>                        queryMode   : 'local',
<a name="cl-2046"></a>                        displayField: 'state',
<a name="cl-2047"></a>                        valueField  : 'state',
<a name="cl-2048"></a>                        editable    : false,
<a name="cl-2049"></a>                        fieldLabel  : 'State',
<a name="cl-2050"></a>                        name        : 'state'
<a name="cl-2051"></a>                    }
<a name="cl-2052"></a>                ]
<a name="cl-2053"></a>            },
<a name="cl-2054"></a>            {
<a name="cl-2055"></a>                id  : 'com_cumulocity_model_control_Clock',
<a name="cl-2056"></a>                name: 'Clock',
<a name="cl-2057"></a>                form: [
<a name="cl-2058"></a>                    {
<a name="cl-2059"></a>                        xtype       : 'timefield',
<a name="cl-2060"></a>                        fieldLabel  : 'Time',
<a name="cl-2061"></a>                        name        : 'time'
<a name="cl-2062"></a>                    }
<a name="cl-2063"></a>                ]
<a name="cl-2064"></a>            }
<a name="cl-2065"></a>        ];
<a name="cl-2066"></a>        ctypes = Ext.Array.merge(ctypes, builtIn);
<a name="cl-2067"></a>        Ext.Array.each(ctypes, function(item) {
<a name="cl-2068"></a>            ctypesMap[item.id] = item;
<a name="cl-2069"></a>        });
<a name="cl-2070"></a>        this.controlTypesList = Ext.Array.map(ctypes, function(i) {return i.id;});
<a name="cl-2071"></a>        return ctypes;
<a name="cl-2072"></a>    }
<a name="cl-2073"></a>});
<a name="cl-2074"></a>Ext.define('C8Y.ux.plugin.Windowble',{
<a name="cl-2075"></a>    extend  : 'Ext.AbstractPlugin',
<a name="cl-2076"></a>    alias   : 'plugin.windowable',
<a name="cl-2077"></a>    requires: [
<a name="cl-2078"></a>        'Ext.window.Window'
<a name="cl-2079"></a>    ],
<a name="cl-2080"></a>    config  : {
<a name="cl-2081"></a>        closable : true,
<a name="cl-2082"></a>        modal    : true,
<a name="cl-2083"></a>		width	 : 450
<a name="cl-2084"></a>    },
<a name="cl-2085"></a>    
<a name="cl-2086"></a>    constructor : function(config) {
<a name="cl-2087"></a>        this.initConfig(config);
<a name="cl-2088"></a>    },
<a name="cl-2089"></a>    
<a name="cl-2090"></a>    init : function(cmp) {
<a name="cl-2091"></a>        if (this.windowed) return;
<a name="cl-2092"></a>        var win = this.win = this.createWindow(cmp);
<a name="cl-2093"></a>        win.show();
<a name="cl-2094"></a>        cmp.on('beforedestroy', this.onBeforeDestroy, this, {single:true});
<a name="cl-2095"></a>    },
<a name="cl-2096"></a>    
<a name="cl-2097"></a>    createWindow : function(cmp) {
<a name="cl-2098"></a>        var win = Ext.create('Ext.window.Window', {
<a name="cl-2099"></a>            width           : cmp.width || this.width || 450,
<a name="cl-2100"></a>            layout          : 'fit',
<a name="cl-2101"></a>            autoHeight      : true,
<a name="cl-2102"></a>            items           : [cmp],
<a name="cl-2103"></a>            preventHeader   : !cmp.title || this.closable,
<a name="cl-2104"></a>            modal           : this.modal,
<a name="cl-2105"></a>            title           : cmp.title || undefined
<a name="cl-2106"></a>        });
<a name="cl-2107"></a>		win.on('show', function(w) {
<a name="cl-2108"></a>			w.el.setOpacity(0);
<a name="cl-2109"></a>			w.el.fadeIn({ duration:600 });
<a name="cl-2110"></a>		}, this, {single:true});
<a name="cl-2111"></a>        return win;
<a name="cl-2112"></a>    },
<a name="cl-2113"></a>    
<a name="cl-2114"></a>    // destroy : function() {
<a name="cl-2115"></a>    //     if (this.win) {
<a name="cl-2116"></a>    //         this.win.destroy();
<a name="cl-2117"></a>    //     }
<a name="cl-2118"></a>    // },
<a name="cl-2119"></a>    
<a name="cl-2120"></a>    onBeforeDestroy : function() {
<a name="cl-2121"></a>        this.win.destroy();
<a name="cl-2122"></a>        return false;
<a name="cl-2123"></a>    }   
<a name="cl-2124"></a>});
<a name="cl-2125"></a>
<a name="cl-2126"></a>/**
<a name="cl-2127"></a>* @class C8Y.ux.ManagedObjectForm
<a name="cl-2128"></a>* Form for Managed Objects
<a name="cl-2129"></a>* @extends Ext.form.Panel
<a name="cl-2130"></a>* @alias c8ymanagedobjectform
<a name="cl-2131"></a>*/
<a name="cl-2132"></a>Ext.define('C8Y.ux.UserForm', {
<a name="cl-2133"></a>    
<a name="cl-2134"></a>    extend : 'Ext.form.Panel',
<a name="cl-2135"></a>    
<a name="cl-2136"></a>    requires : [
<a name="cl-2137"></a>        'Ext.form.field.Text',
<a name="cl-2138"></a>        'Ext.form.field.Display',
<a name="cl-2139"></a>        'Ext.form.field.Checkbox',
<a name="cl-2140"></a>        'Ext.form.field.ComboBox',
<a name="cl-2141"></a>        'Ext.layout.container.Anchor',
<a name="cl-2142"></a>        'Ext.layout.container.Column',
<a name="cl-2143"></a>        'C8Y.model.User',
<a name="cl-2144"></a>        'C8Y.ux.plugin.Panel',
<a name="cl-2145"></a>        'C8Y.ux.plugin.Windowble',
<a name="cl-2146"></a>		'C8Y.ux.UserRoleFieldSet'
<a name="cl-2147"></a>    ],
<a name="cl-2148"></a>    plugins : [
<a name="cl-2149"></a>        { ptype : 'c8ypanel'},
<a name="cl-2150"></a>        { ptype : 'windowable', closable : false, width: 750 }
<a name="cl-2151"></a>    ],
<a name="cl-2152"></a>    alias   : 'widget.c8yuserform',
<a name="cl-2153"></a>    
<a name="cl-2154"></a>    initComponent : function() {
<a name="cl-2155"></a>        this.items = this.buildItems();
<a name="cl-2156"></a>        this.dockedItems = [
<a name="cl-2157"></a>            {
<a name="cl-2158"></a>                xtype   : 'toolbar',
<a name="cl-2159"></a>                dock    : 'bottom',
<a name="cl-2160"></a>                ui      : 'footer',
<a name="cl-2161"></a>                items   : this.buildButtons()
<a name="cl-2162"></a>            }
<a name="cl-2163"></a>        ];
<a name="cl-2164"></a>        this.padding = 10;
<a name="cl-2165"></a>        this.defaultType = 'textfield';
<a name="cl-2166"></a>        this.fieldDefaults = {
<a name="cl-2167"></a>            labelAlign: 'top',
<a name="cl-2168"></a>            anchor  : '100%'
<a name="cl-2169"></a>        };
<a name="cl-2170"></a>        this.whiteBg = true;
<a name="cl-2171"></a>        this.layout = 'anchor';
<a name="cl-2172"></a>        this.autoScroll = true;
<a name="cl-2173"></a>        this.style = "border:none;background-color:#FFF";
<a name="cl-2174"></a>        this.bodyStyle = "border:none";
<a name="cl-2175"></a>        this.callParent(arguments);
<a name="cl-2176"></a>    },
<a name="cl-2177"></a>
<a name="cl-2178"></a>	buildItems     : function() {
<a name="cl-2179"></a>		return {
<a name="cl-2180"></a>			xtype	: 'container',
<a name="cl-2181"></a>			layout	: 'column',
<a name="cl-2182"></a>			items	: [
<a name="cl-2183"></a>				{
<a name="cl-2184"></a>					xtype		: 'container',
<a name="cl-2185"></a>					columnWidth	: 0.6,
<a name="cl-2186"></a>					layout		: 'anchor',
<a name="cl-2187"></a>					defaults	: {
<a name="cl-2188"></a>						labelAlign  : 'top',
<a name="cl-2189"></a>						anchor		: '96%',
<a name="cl-2190"></a>						xtype		: 'textfield'
<a name="cl-2191"></a>					},
<a name="cl-2192"></a>					items		: this.buildMainItems() 
<a name="cl-2193"></a>				},
<a name="cl-2194"></a>				{
<a name="cl-2195"></a>					xtype		: 'container',
<a name="cl-2196"></a>					columnWidth	: 0.4,
<a name="cl-2197"></a>					layout		: 'anchor',
<a name="cl-2198"></a>					items		: {
<a name="cl-2199"></a>						xtype	: 'c8yuserrolefieldset'
<a name="cl-2200"></a>					}
<a name="cl-2201"></a>				}
<a name="cl-2202"></a>			]
<a name="cl-2203"></a>		};
<a name="cl-2204"></a>	},
<a name="cl-2205"></a>    
<a name="cl-2206"></a>    buildMainItems : function() {
<a name="cl-2207"></a>        return  [
<a name="cl-2208"></a>            {
<a name="cl-2209"></a>                fieldLabel  : 'User Name',
<a name="cl-2210"></a>                name        : 'userName',
<a name="cl-2211"></a>                itemId      : 'username_field',
<a name="cl-2212"></a>                allowBlank  : false
<a name="cl-2213"></a>            },
<a name="cl-2214"></a>            {
<a name="cl-2215"></a>                fieldLabel  : 'Email',
<a name="cl-2216"></a>                name        : 'email',
<a name="cl-2217"></a>                vtype       : 'email'
<a name="cl-2218"></a>            },
<a name="cl-2219"></a>            {
<a name="cl-2220"></a>                fieldLabel  : 'First Name',
<a name="cl-2221"></a>                name        : 'firstName'
<a name="cl-2222"></a>            },
<a name="cl-2223"></a>            {
<a name="cl-2224"></a>                fieldLabel  : 'Last Name',
<a name="cl-2225"></a>                name        : 'lastName'
<a name="cl-2226"></a>            },
<a name="cl-2227"></a>            {
<a name="cl-2228"></a>                fieldLabel  : 'Phone',
<a name="cl-2229"></a>                name        : 'phone'
<a name="cl-2230"></a>            },
<a name="cl-2231"></a>            {
<a name="cl-2232"></a>                xtype       : 'checkboxfield',
<a name="cl-2233"></a>                fieldLabel  : 'Enabled',
<a name="cl-2234"></a>                name        : 'enabled',
<a name="cl-2235"></a>                labelAlign  : 'left',
<a name="cl-2236"></a>                inputValue  : true
<a name="cl-2237"></a>            },
<a name="cl-2238"></a>            {
<a name="cl-2239"></a>                fieldLabel  : 'New Password',
<a name="cl-2240"></a>                inputType   : 'password',
<a name="cl-2241"></a>                name        : 'new_password',
<a name="cl-2242"></a>                margin      : '20 0 0 0',
<a name="cl-2243"></a>                itemId      : 'confirmpassword'
<a name="cl-2244"></a>            },
<a name="cl-2245"></a>            {
<a name="cl-2246"></a>                fieldLabel  : 'Confirm new password',
<a name="cl-2247"></a>                inputType   : 'password',
<a name="cl-2248"></a>                name        : 'confirm_new_password',
<a name="cl-2249"></a>                initialPassField: 'confirmpassword',
<a name="cl-2250"></a>                vtype       : 'password'
<a name="cl-2251"></a>                
<a name="cl-2252"></a>            },
<a name="cl-2253"></a>            {
<a name="cl-2254"></a>                fieldLabel  : 'Groups',
<a name="cl-2255"></a>                xtype       : 'combo',
<a name="cl-2256"></a>                queryMode   : 'local',
<a name="cl-2257"></a>                store       : Ext.getStore('c8yusergroup'),
<a name="cl-2258"></a>                displayField: 'name',
<a name="cl-2259"></a>                name        : 'groups',
<a name="cl-2260"></a>                valueField  : 'id',
<a name="cl-2261"></a>                multiSelect : true,
<a name="cl-2262"></a>                itemId      : 'groups_field'
<a name="cl-2263"></a>            }
<a name="cl-2264"></a>        ];
<a name="cl-2265"></a>    },
<a name="cl-2266"></a>    
<a name="cl-2267"></a>    buildButtons : function() {
<a name="cl-2268"></a>       return  [
<a name="cl-2269"></a>            {
<a name="cl-2270"></a>                text    : 'Save and Close',
<a name="cl-2271"></a>                handler : this.onSave,
<a name="cl-2272"></a>                scope   : this,
<a name="cl-2273"></a>                scale   : 'medium',
<a name="cl-2274"></a>                cls     : 'mainAction'
<a name="cl-2275"></a>            },
<a name="cl-2276"></a>            {
<a name="cl-2277"></a>                text    : 'Cancel',
<a name="cl-2278"></a>                handler : this.onCancel,
<a name="cl-2279"></a>                scope   : this,
<a name="cl-2280"></a>                scale   : 'medium',
<a name="cl-2281"></a>                cls     : 'mainAction'
<a name="cl-2282"></a>            }
<a name="cl-2283"></a>       ];
<a name="cl-2284"></a>    },
<a name="cl-2285"></a>    
<a name="cl-2286"></a>    /**
<a name="cl-2287"></a>    * Loads a User
<a name="cl-2288"></a>    * @param mo {C8Y.model.User/Number} A User or numeric id
<a name="cl-2289"></a>    */
<a name="cl-2290"></a>    loadUser : function(mo) {
<a name="cl-2291"></a>        var id = Ext.isNumeric(mo) ? mo : mo.get('id'),
<a name="cl-2292"></a>            me = this,
<a name="cl-2293"></a>            rec;
<a name="cl-2294"></a>       
<a name="cl-2295"></a>        me.el.mask('Loading');
<a name="cl-2296"></a>        this.query('#username_field').shift().disable();
<a name="cl-2297"></a>        this.query('#confirmpassword').shift().allowBlank = true;
<a name="cl-2298"></a>        mo.load(function() {
<a name="cl-2299"></a>            me.loadRecord(mo);
<a name="cl-2300"></a>            me.getForm().setValues({groups:mo.getGroupsIdArray(), roles: mo.getRolesIdArray()});
<a name="cl-2301"></a>            me.el.unmask();
<a name="cl-2302"></a>        });
<a name="cl-2303"></a>    },
<a name="cl-2304"></a>    
<a name="cl-2305"></a>    /**
<a name="cl-2306"></a>    * Create a new record to be saved
<a name="cl-2307"></a>    */
<a name="cl-2308"></a>    createNewRecord : function() {
<a name="cl-2309"></a>        var newRecord = Ext.create('C8Y.model.User');
<a name="cl-2310"></a>        this.query('#confirmpassword').shift().allowBlank = false;
<a name="cl-2311"></a>        this.query('#username_field').shift().enable();
<a name="cl-2312"></a>        this.loadRecord(newRecord);
<a name="cl-2313"></a>    },
<a name="cl-2314"></a>        
<a name="cl-2315"></a>    onSave : function() {
<a name="cl-2316"></a>        var me = this,
<a name="cl-2317"></a>            values = this.getValues(),
<a name="cl-2318"></a>            record = me.getRecord(),
<a name="cl-2319"></a>            isNew = !record.get('id'),
<a name="cl-2320"></a>            form = this.getForm();
<a name="cl-2321"></a>            
<a name="cl-2322"></a>        if (form.isValid()) {
<a name="cl-2323"></a>			var roles = values.roles ? (Ext.isArray(values.roles) ? values.roles : [values.roles]) : [];
<a name="cl-2324"></a>            me.el.mask('Saving');
<a name="cl-2325"></a>            values.enabled = values.enabled || false;
<a name="cl-2326"></a>            if (values.new_password) values.password = values.new_password;
<a name="cl-2327"></a>			delete values.roles;
<a name="cl-2328"></a>            delete values.id;
<a name="cl-2329"></a>            delete values.new_password;
<a name="cl-2330"></a>            delete values.confirm_new_password;
<a name="cl-2331"></a>            record.set(values);
<a name="cl-2332"></a>            record.save(function() {
<a name="cl-2333"></a>                if (isNew) {
<a name="cl-2334"></a>                    //handle the bug of not having id
<a name="cl-2335"></a>                    if (!record.get('id')) {
<a name="cl-2336"></a>                        record.set('id', record.get('userName'));
<a name="cl-2337"></a>                        record.commit();
<a name="cl-2338"></a>                    }
<a name="cl-2339"></a>                    me.fireEvent('created', record);
<a name="cl-2340"></a>                }
<a name="cl-2341"></a>                
<a name="cl-2342"></a>                // Until the bug is solved this has to be called here
<a name="cl-2343"></a>                var hasGroupsToUpdate = record.updateGroups(values.groups);
<a name="cl-2344"></a>                var hasRolesToUpdate = record.updateRoles(roles);
<a name="cl-2345"></a>                
<a name="cl-2346"></a>                if (hasGroupsToUpdate) {
<a name="cl-2347"></a>                    record.on('groupactioncomplete', function() {
<a name="cl-2348"></a>                        me.destroy();
<a name="cl-2349"></a>                    }, this, {single:true});    
<a name="cl-2350"></a>                } else {
<a name="cl-2351"></a>                    record.load();
<a name="cl-2352"></a>                    me.destroy();
<a name="cl-2353"></a>                }
<a name="cl-2354"></a>            });
<a name="cl-2355"></a>        }                    
<a name="cl-2356"></a>        
<a name="cl-2357"></a>    },
<a name="cl-2358"></a>    
<a name="cl-2359"></a>    onCancel : function() {
<a name="cl-2360"></a>        this.destroy();
<a name="cl-2361"></a>    },
<a name="cl-2362"></a>    
<a name="cl-2363"></a>    onCreateNew : function() {
<a name="cl-2364"></a>        this.createNewRecord();
<a name="cl-2365"></a>    }
<a name="cl-2366"></a>});
<a name="cl-2367"></a>/**
<a name="cl-2368"></a> * @class C8Y.ux.UserGrid
<a name="cl-2369"></a> * Creates an InventoryGrid
<a name="cl-2370"></a> * @extends Ext.grid.Panel
<a name="cl-2371"></a> */
<a name="cl-2372"></a>Ext.define('C8Y.ux.UserGrid', {
<a name="cl-2373"></a>    extend  : 'Ext.grid.Panel',
<a name="cl-2374"></a>    alias   : 'widget.c8yusergrid',
<a name="cl-2375"></a>    requires: [
<a name="cl-2376"></a>        'Ext.toolbar.Paging',
<a name="cl-2377"></a>        'Ext.selection.CheckboxModel',
<a name="cl-2378"></a>        'Ext.window.MessageBox',
<a name="cl-2379"></a>        'C8Y.store.User',
<a name="cl-2380"></a>        'C8Y.ux.UserForm',
<a name="cl-2381"></a>        'C8Y.ux.plugin.Panel',
<a name="cl-2382"></a>        'C8Y.ux.PanelFeatures'
<a name="cl-2383"></a>    ],
<a name="cl-2384"></a>    
<a name="cl-2385"></a>    mixins  : {
<a name="cl-2386"></a>        feat    : 'C8Y.ux.PanelFeatures'
<a name="cl-2387"></a>    },
<a name="cl-2388"></a>    
<a name="cl-2389"></a>    plugins : [
<a name="cl-2390"></a>        { ptype: 'c8ypanel'}
<a name="cl-2391"></a>    ],
<a name="cl-2392"></a>  
<a name="cl-2393"></a>  	/**
<a name="cl-2394"></a>	 * Automatic Method to initialize the grid, shouldn't be called by the developer
<a name="cl-2395"></a>	 * @method
<a name="cl-2396"></a>	 */
<a name="cl-2397"></a>    initComponent : function() {
<a name="cl-2398"></a>        var me = this;
<a name="cl-2399"></a>        
<a name="cl-2400"></a>        this.selModel = Ext.create('Ext.selection.CheckboxModel');
<a name="cl-2401"></a>        this.columns = this.buildColumns();
<a name="cl-2402"></a>        this.store = Ext.getStore('c8yuser') || Ext.create('C8Y.store.User', {storeId: 'c8yuser', autoLoad: true});
<a name="cl-2403"></a>        this.dockedItems = [ this.buildTopActionMenu(this.buildTopMenuItems()) ];
<a name="cl-2404"></a>        this.title = 'User List'; 
<a name="cl-2405"></a>        //Edit on double click
<a name="cl-2406"></a>        this.on('itemdblclick', function(v, r) { me.detailUser(r); });
<a name="cl-2407"></a>        this.callParent(arguments);
<a name="cl-2408"></a>		this.store.clearFilter();
<a name="cl-2409"></a>    },
<a name="cl-2410"></a>    
<a name="cl-2411"></a>    buildColumns : function() {
<a name="cl-2412"></a>        return [
<a name="cl-2413"></a>            
<a name="cl-2414"></a>            { header: 'Username',  dataIndex: 'userName', width:120},
<a name="cl-2415"></a>    		{ header: 'First Name',  dataIndex: 'firstName', width: 120},
<a name="cl-2416"></a>    		{ header: 'Last Name',  dataIndex: 'lastName', width: 120 },
<a name="cl-2417"></a>    		{ header: 'Email',  dataIndex: 'email', width:200 },
<a name="cl-2418"></a>    		{ header: 'Associated Groups', flex:1, 
<a name="cl-2419"></a>                renderer : function(val, meta, record) {
<a name="cl-2420"></a>                    var strarr = [],
<a name="cl-2421"></a>                        g = record.groups();
<a name="cl-2422"></a>                    
<a name="cl-2423"></a>                    g.each(function(item) {
<a name="cl-2424"></a>                        strarr.push(item.get('name'));    
<a name="cl-2425"></a>                    });
<a name="cl-2426"></a>
<a name="cl-2427"></a>                    return strarr.join(', ');
<a name="cl-2428"></a>                }
<a name="cl-2429"></a>            },
<a name="cl-2430"></a>    		{ header: 'Status', dataIndex: 'enabled',  width: 80, align:'center', 
<a name="cl-2431"></a>                renderer : function(val, meta) { 
<a name="cl-2432"></a>                    var color = val ? '#3CAA00' : "#E6E6E6",
<a name="cl-2433"></a>                        txtcolor = val ? '#FFF' : "#000",
<a name="cl-2434"></a>                        txt   = val ? 'Active' : 'Suspended';
<a name="cl-2435"></a>                    meta.style = Ext.String.format('background-color:{0}; color: {1}', color, txtcolor);
<a name="cl-2436"></a>                    return txt;
<a name="cl-2437"></a>                    // return Ext.String.format('&lt;span class="roundcorners status{0}"&gt;{0}&lt;/span&gt;', (val ? 'Enabled' : 'Disabled'))
<a name="cl-2438"></a>                }
<a name="cl-2439"></a>            }
<a name="cl-2440"></a>        ];
<a name="cl-2441"></a>    },
<a name="cl-2442"></a>    
<a name="cl-2443"></a>    buildTopMenuItems : function() {
<a name="cl-2444"></a>        var me = this;
<a name="cl-2445"></a>        return [
<a name="cl-2446"></a>                {
<a name="cl-2447"></a>                    text    : 'Add User',
<a name="cl-2448"></a>                    handler : this.onAddUser
<a name="cl-2449"></a>                },
<a name="cl-2450"></a>                {
<a name="cl-2451"></a>                    text    : 'Actions',
<a name="cl-2452"></a>                    menu    : { 
<a name="cl-2453"></a>                        plain  : true,
<a name="cl-2454"></a>                        defaults : {'cls': 'action', width:150}, 
<a name="cl-2455"></a>                        items : [
<a name="cl-2456"></a>                            { text : 'Edit User', scope: this, handler: this.onEditUser},
<a name="cl-2457"></a>                            { text : 'Suspend User', scope: this, handler: this.onSuspendUser },
<a name="cl-2458"></a>                            { text : 'Remove User', scope: this, handler: this.onDeleteUser }
<a name="cl-2459"></a>                        ]
<a name="cl-2460"></a>                    }
<a name="cl-2461"></a>                },
<a name="cl-2462"></a>                {
<a name="cl-2463"></a>                    xtype   : 'textfield',
<a name="cl-2464"></a>                    height  : 24,
<a name="cl-2465"></a>                    emptyText : 'Search By Name',
<a name="cl-2466"></a>                    cls     : '',
<a name="cl-2467"></a>                    itemId  : 'searchField',
<a name="cl-2468"></a>                    listeners: {
<a name="cl-2469"></a>                        specialkey: function(field, e){
<a name="cl-2470"></a>                            if (e.getKey() == e.ENTER) {
<a name="cl-2471"></a>                                me.searchUser(field.getValue());
<a name="cl-2472"></a>                            }
<a name="cl-2473"></a>                        }
<a name="cl-2474"></a>                    }
<a name="cl-2475"></a>                }
<a name="cl-2476"></a>        ];
<a name="cl-2477"></a>    },
<a name="cl-2478"></a>    
<a name="cl-2479"></a>    addUser : function() {
<a name="cl-2480"></a>        this.detailUser();
<a name="cl-2481"></a>    },
<a name="cl-2482"></a>    
<a name="cl-2483"></a>    searchUser : function(searchStr) {
<a name="cl-2484"></a>        var str = this.getStore(),
<a name="cl-2485"></a>            me = this;
<a name="cl-2486"></a>        this.getComponent('topDock').add({
<a name="cl-2487"></a>            xtype   : 'button',
<a name="cl-2488"></a>            text    : 'Remove Filter',
<a name="cl-2489"></a>            itemId  : 'removeFilter',
<a name="cl-2490"></a>            scale   : 'small',
<a name="cl-2491"></a>            scope   : this,
<a name="cl-2492"></a>            handler : function() {
<a name="cl-2493"></a>                str.clearFilter();
<a name="cl-2494"></a>                me.getComponent('topDock').getComponent('removeFilter').destroy();
<a name="cl-2495"></a>                me.getComponent('topDock').getComponent('searchField').setValue('');
<a name="cl-2496"></a>            }
<a name="cl-2497"></a>        })
<a name="cl-2498"></a>        str.clearFilter();
<a name="cl-2499"></a>        str.filterBy(function(record) {
<a name="cl-2500"></a>            var rg = new RegExp(searchStr,'i'),
<a name="cl-2501"></a>                firstName = record.get('firstName'),
<a name="cl-2502"></a>                lastName = record.get('lastName');
<a name="cl-2503"></a>            return (firstName &amp;&amp; firstName.match(rg)) || (lastName &amp;&amp; lastName.match(rg));
<a name="cl-2504"></a>        })
<a name="cl-2505"></a>    },
<a name="cl-2506"></a>    
<a name="cl-2507"></a>    detailUser : function(user) {
<a name="cl-2508"></a>        var title = user ? "Edit User " + user.get('userName') : 'Create new User',
<a name="cl-2509"></a>            store = this.getStore(),
<a name="cl-2510"></a>            form = Ext.create('C8Y.ux.UserForm', {
<a name="cl-2511"></a>                windowed: true,
<a name="cl-2512"></a>                title   : title,
<a name="cl-2513"></a>                listeners : {
<a name="cl-2514"></a>                    'render' : function(form) {
<a name="cl-2515"></a>                        if (user) {
<a name="cl-2516"></a>                            form.loadUser(user);
<a name="cl-2517"></a>                        } else {
<a name="cl-2518"></a>                            form.createNewRecord();
<a name="cl-2519"></a>                        }
<a name="cl-2520"></a>                    },
<a name="cl-2521"></a>                    'created' : function(rec) {
<a name="cl-2522"></a>                        store.add(rec);
<a name="cl-2523"></a>                    }
<a name="cl-2524"></a>                }
<a name="cl-2525"></a>            });
<a name="cl-2526"></a>        return form;
<a name="cl-2527"></a>    },
<a name="cl-2528"></a>    
<a name="cl-2529"></a>    disableUsers : function(selection) {
<a name="cl-2530"></a>        Ext.Array.each(selection, function(user) {
<a name="cl-2531"></a>            user.disable();
<a name="cl-2532"></a>        });
<a name="cl-2533"></a>    },
<a name="cl-2534"></a>    
<a name="cl-2535"></a>    deleteUsers : function(selection) {
<a name="cl-2536"></a>        Ext.Array.each(selection, function(user) {
<a name="cl-2537"></a>            user.destroy();
<a name="cl-2538"></a>        });
<a name="cl-2539"></a>    },
<a name="cl-2540"></a>    
<a name="cl-2541"></a>    //Event Listeners
<a name="cl-2542"></a>    onAddUser : function() {
<a name="cl-2543"></a>        this.addUser();
<a name="cl-2544"></a>    },
<a name="cl-2545"></a>    
<a name="cl-2546"></a>    onEditUser : function() {
<a name="cl-2547"></a>        var sel = this.getSelectionModel().getSelection(),
<a name="cl-2548"></a>            selRecord = sel.shift();
<a name="cl-2549"></a>        
<a name="cl-2550"></a>        if (selRecord) {
<a name="cl-2551"></a>            this.detailUser(selRecord);
<a name="cl-2552"></a>        }
<a name="cl-2553"></a>    },
<a name="cl-2554"></a>    
<a name="cl-2555"></a>    onSuspendUser : function() {
<a name="cl-2556"></a>         var sel = this.getSelectionModel().getSelection(),
<a name="cl-2557"></a>             qt = sel.length,
<a name="cl-2558"></a>             me = this;
<a name="cl-2559"></a>         if (qt) {
<a name="cl-2560"></a>             Ext.Msg.confirm(
<a name="cl-2561"></a>                'Disable users',
<a name="cl-2562"></a>                Ext.String.format('Do you want to disable {0} user{1}?', qt, (qt &gt; 1 ? 's':'')),
<a name="cl-2563"></a>                function(btn) {
<a name="cl-2564"></a>                    if (btn == 'yes') {
<a name="cl-2565"></a>                        me.disableUsers(sel);
<a name="cl-2566"></a>                    }
<a name="cl-2567"></a>                }
<a name="cl-2568"></a>             );
<a name="cl-2569"></a>         }
<a name="cl-2570"></a>    },
<a name="cl-2571"></a>    
<a name="cl-2572"></a>    onDeleteUser : function() {
<a name="cl-2573"></a>        var sel = this.getSelectionModel().getSelection(),
<a name="cl-2574"></a>             qt = sel.length,
<a name="cl-2575"></a>             me = this;
<a name="cl-2576"></a>         if (qt) {
<a name="cl-2577"></a>             Ext.Msg.confirm(
<a name="cl-2578"></a>                'Delete users',
<a name="cl-2579"></a>                Ext.String.format('Do you want to delete {0} user{1}?', qt, (qt &gt; 1 ? 's':'')),
<a name="cl-2580"></a>                function(btn) {
<a name="cl-2581"></a>                    if (btn == 'yes') {
<a name="cl-2582"></a>                        me.deleteUsers(sel);
<a name="cl-2583"></a>                    }
<a name="cl-2584"></a>                }
<a name="cl-2585"></a>             );
<a name="cl-2586"></a>         }
<a name="cl-2587"></a>    }
<a name="cl-2588"></a>});
<a name="cl-2589"></a>/**
<a name="cl-2590"></a> * @class C8Y.ux.UserRoleGrid
<a name="cl-2591"></a> * Creates an InventoryGrid
<a name="cl-2592"></a> * @extends Ext.grid.Panel
<a name="cl-2593"></a> */
<a name="cl-2594"></a>Ext.define('C8Y.ux.UserRoleGrid', {
<a name="cl-2595"></a>    extend  : 'Ext.grid.Panel',
<a name="cl-2596"></a>    alias   : 'widget.c8yuserrolegrid',
<a name="cl-2597"></a>    requires: [
<a name="cl-2598"></a>        'Ext.toolbar.Paging',
<a name="cl-2599"></a>        'C8Y.store.UserRole',
<a name="cl-2600"></a>        'C8Y.ux.UserForm',
<a name="cl-2601"></a>        'Ext.selection.CheckboxModel',
<a name="cl-2602"></a>        'Ext.window.MessageBox'
<a name="cl-2603"></a>    ],
<a name="cl-2604"></a>    mixins  : {
<a name="cl-2605"></a>        feat    : 'C8Y.ux.PanelFeatures'
<a name="cl-2606"></a>    },
<a name="cl-2607"></a>    plugins : [
<a name="cl-2608"></a>        {ptype: 'c8ypanel'}
<a name="cl-2609"></a>    ],
<a name="cl-2610"></a>  	/**
<a name="cl-2611"></a>	 * Automatic Method to initialize the grid, shouldn't be called by the developer
<a name="cl-2612"></a>	 * @method
<a name="cl-2613"></a>	 */
<a name="cl-2614"></a>    initComponent : function() {
<a name="cl-2615"></a>        var me = this;
<a name="cl-2616"></a>
<a name="cl-2617"></a>        this.padding = 20;
<a name="cl-2618"></a>        this.columns = this.buildColumns();
<a name="cl-2619"></a>        this.store = Ext.getStore('c8yuserrole') || Ext.create('C8Y.store.UserRole', {storeId: 'c8yuserrole', autoLoad:true});
<a name="cl-2620"></a>        //this.dockedItems = [this.buildTopActionMenu(this.buildActionMenuItems())];
<a name="cl-2621"></a>        this.title = 'User Role List';
<a name="cl-2622"></a>        this.viewConfig = this.getDefaultGridView();
<a name="cl-2623"></a>        
<a name="cl-2624"></a>        this.callParent(arguments);
<a name="cl-2625"></a>    },
<a name="cl-2626"></a>    
<a name="cl-2627"></a>    buildColumns : function() {
<a name="cl-2628"></a>        return [
<a name="cl-2629"></a>            { header : 'ID', width: 40, align:'center', dataIndex:'id'},
<a name="cl-2630"></a>            { header : 'Name', flex : 1, dataIndex:'name'}
<a name="cl-2631"></a>        ];
<a name="cl-2632"></a>    },
<a name="cl-2633"></a>    
<a name="cl-2634"></a>    buildActionMenuItems : function() {
<a name="cl-2635"></a>        var me = this;
<a name="cl-2636"></a>        return [
<a name="cl-2637"></a>                {
<a name="cl-2638"></a>                    text    : 'Add User Role'
<a name="cl-2639"></a>                },
<a name="cl-2640"></a>                {
<a name="cl-2641"></a>                    text    : 'Actions',
<a name="cl-2642"></a>                    menu    : { 
<a name="cl-2643"></a>                        plain       : true,
<a name="cl-2644"></a>                        defaults    : {'cls': 'action', width:150},
<a name="cl-2645"></a>                        items       : [
<a name="cl-2646"></a>                            { text : 'Edit User Role' },
<a name="cl-2647"></a>                            { text : 'Remove User Role' }
<a name="cl-2648"></a>                    ]}
<a name="cl-2649"></a>                },
<a name="cl-2650"></a>                {
<a name="cl-2651"></a>                    xtype   : 'textfield',
<a name="cl-2652"></a>                    height  : 24,
<a name="cl-2653"></a>                    cls     : '',
<a name="cl-2654"></a>                    emptyText : 'Search By Name',
<a name="cl-2655"></a>                    itemId  : 'searchField',
<a name="cl-2656"></a>                    listeners: {
<a name="cl-2657"></a>                        specialkey: function(field, e){
<a name="cl-2658"></a>                            if (e.getKey() == e.ENTER) {
<a name="cl-2659"></a>                                me.searchUserRole(field.getValue());
<a name="cl-2660"></a>                            }
<a name="cl-2661"></a>                        }
<a name="cl-2662"></a>                    }
<a name="cl-2663"></a>                }
<a name="cl-2664"></a>        ];
<a name="cl-2665"></a>    },
<a name="cl-2666"></a>    
<a name="cl-2667"></a>    searchUserRole : function(searchStr) {
<a name="cl-2668"></a>        var str = this.getStore(),
<a name="cl-2669"></a>            me = this;
<a name="cl-2670"></a>        this.getComponent('topDock').add({
<a name="cl-2671"></a>            xtype   : 'button',
<a name="cl-2672"></a>            text    : 'Remove Filter',
<a name="cl-2673"></a>            itemId  : 'removeFilter',
<a name="cl-2674"></a>            scale   : 'small',
<a name="cl-2675"></a>            scope   : this,
<a name="cl-2676"></a>            handler : function() {
<a name="cl-2677"></a>                str.clearFilter();
<a name="cl-2678"></a>                me.getComponent('topDock').getComponent('removeFilter').destroy();
<a name="cl-2679"></a>                me.getComponent('topDock').getComponent('searchField').setValue('');
<a name="cl-2680"></a>            }
<a name="cl-2681"></a>        })
<a name="cl-2682"></a>        str.clearFilter();
<a name="cl-2683"></a>        str.filterBy(function(record) {
<a name="cl-2684"></a>            var rg = new RegExp(searchStr,'i'),
<a name="cl-2685"></a>                name = record.get('name');
<a name="cl-2686"></a>            return (name &amp;&amp; name.match(rg));
<a name="cl-2687"></a>        })
<a name="cl-2688"></a>    }
<a name="cl-2689"></a>});
<a name="cl-2690"></a>/**
<a name="cl-2691"></a>* @class C8Y.ux.ManagedObjectForm
<a name="cl-2692"></a>* Form for Managed Objects
<a name="cl-2693"></a>* @extends Ext.form.Panel
<a name="cl-2694"></a>* @alias c8ymanagedobjectform
<a name="cl-2695"></a>*/
<a name="cl-2696"></a>Ext.define('C8Y.ux.UserGroupForm', {
<a name="cl-2697"></a>    
<a name="cl-2698"></a>    extend : 'Ext.form.Panel',
<a name="cl-2699"></a>    
<a name="cl-2700"></a>    requires : [
<a name="cl-2701"></a>        'Ext.form.field.Text',
<a name="cl-2702"></a>        'Ext.form.field.Display',
<a name="cl-2703"></a>        'Ext.form.field.Checkbox',
<a name="cl-2704"></a>        'Ext.layout.container.Anchor',
<a name="cl-2705"></a>        'Ext.layout.container.Column',
<a name="cl-2706"></a>        'C8Y.model.User',
<a name="cl-2707"></a>        'C8Y.ux.plugin.Panel',
<a name="cl-2708"></a>        'C8Y.ux.plugin.Windowble',
<a name="cl-2709"></a>        'C8Y.ux.UserRoleFieldSet'
<a name="cl-2710"></a>    ],
<a name="cl-2711"></a>    plugins : [
<a name="cl-2712"></a>        { ptype : 'c8ypanel'},
<a name="cl-2713"></a>        { ptype : 'windowable', closable : false}
<a name="cl-2714"></a>    ],
<a name="cl-2715"></a>    alias   : 'widget.c8yusergroupform',
<a name="cl-2716"></a>    
<a name="cl-2717"></a>    initComponent : function() {
<a name="cl-2718"></a>        this.items = this.buildItems();
<a name="cl-2719"></a>        this.dockedItems = [
<a name="cl-2720"></a>            {
<a name="cl-2721"></a>                xtype   : 'toolbar',
<a name="cl-2722"></a>                dock    : 'bottom',
<a name="cl-2723"></a>                ui      : 'footer',
<a name="cl-2724"></a>                items   : this.buildButtons()
<a name="cl-2725"></a>            }
<a name="cl-2726"></a>        ]; 
<a name="cl-2727"></a>        this.padding = 10;
<a name="cl-2728"></a>        this.defaultType = 'textfield';
<a name="cl-2729"></a>        this.fieldDefaults = {
<a name="cl-2730"></a>            labelAlign: 'top',
<a name="cl-2731"></a>            anchor  : '100%'
<a name="cl-2732"></a>        };
<a name="cl-2733"></a>        this.whiteBg = true;
<a name="cl-2734"></a>        this.layout = 'anchor';
<a name="cl-2735"></a>        this.autoScroll = true;
<a name="cl-2736"></a>        this.style = "border:none;background-color:#FFF";
<a name="cl-2737"></a>        this.bodyStyle = "border:none";
<a name="cl-2738"></a>        this.callParent(arguments);
<a name="cl-2739"></a>    },
<a name="cl-2740"></a>    
<a name="cl-2741"></a>    buildItems : function() {
<a name="cl-2742"></a>        return  [
<a name="cl-2743"></a>            {
<a name="cl-2744"></a>                fieldLabel  : 'Name',
<a name="cl-2745"></a>                name        : 'name',
<a name="cl-2746"></a>                itemId      : 'name'
<a name="cl-2747"></a>            },
<a name="cl-2748"></a>            {
<a name="cl-2749"></a>                xtype       : 'c8yuserrolefieldset',
<a name="cl-2750"></a>                itemId      : 'roles'
<a name="cl-2751"></a>            }
<a name="cl-2752"></a>        ];
<a name="cl-2753"></a>    },
<a name="cl-2754"></a>    
<a name="cl-2755"></a>    buildButtons : function() {
<a name="cl-2756"></a>       return  [
<a name="cl-2757"></a>            {
<a name="cl-2758"></a>                text    : 'Save and Close',
<a name="cl-2759"></a>                handler : this.onSave,
<a name="cl-2760"></a>                scope   : this,
<a name="cl-2761"></a>                scale   : 'medium',
<a name="cl-2762"></a>                cls     : 'mainAction'
<a name="cl-2763"></a>            },
<a name="cl-2764"></a>            {
<a name="cl-2765"></a>                text    : 'Cancel',
<a name="cl-2766"></a>                handler : this.onCancel,
<a name="cl-2767"></a>                scope   : this,
<a name="cl-2768"></a>                scale   : 'medium',
<a name="cl-2769"></a>                cls     : 'mainAction'
<a name="cl-2770"></a>            }
<a name="cl-2771"></a>       ];
<a name="cl-2772"></a>    },
<a name="cl-2773"></a>    
<a name="cl-2774"></a>    /**
<a name="cl-2775"></a>    * Loads a Group
<a name="cl-2776"></a>    * @param mo {C8Y.model.UserGroup/Number} A User or numeric id
<a name="cl-2777"></a>    */
<a name="cl-2778"></a>    loadUserGroup : function(mo) {
<a name="cl-2779"></a>        var id = Ext.isNumeric(mo) ? mo : mo.get('id'),
<a name="cl-2780"></a>            me = this,
<a name="cl-2781"></a>            rec;
<a name="cl-2782"></a>       
<a name="cl-2783"></a>        me.el.mask('Loading');
<a name="cl-2784"></a>        mo.load(function() {
<a name="cl-2785"></a>            me.loadRecord(mo);
<a name="cl-2786"></a>            me.getForm().setValues({roles:mo.getRolesIdArray()});
<a name="cl-2787"></a>            me.el.unmask();
<a name="cl-2788"></a>        });
<a name="cl-2789"></a>    },
<a name="cl-2790"></a>    
<a name="cl-2791"></a>    /**
<a name="cl-2792"></a>    * Create a new record to be saved
<a name="cl-2793"></a>    */
<a name="cl-2794"></a>    createNewRecord : function() {
<a name="cl-2795"></a>        var newRecord = Ext.create('C8Y.model.UserGroup');
<a name="cl-2796"></a>        this.loadRecord(newRecord);
<a name="cl-2797"></a>    },
<a name="cl-2798"></a>        
<a name="cl-2799"></a>    onSave : function() {
<a name="cl-2800"></a>        var me = this,
<a name="cl-2801"></a>            values = this.getValues(),
<a name="cl-2802"></a>            record = me.getRecord(),
<a name="cl-2803"></a>            id = record.get('id');
<a name="cl-2804"></a>                            
<a name="cl-2805"></a>        me.el.mask('Saving');
<a name="cl-2806"></a>    
<a name="cl-2807"></a>        delete values.id;
<a name="cl-2808"></a>        record.updateRoles(Ext.isArray(values.roles) ? values.roles : [values.roles]);
<a name="cl-2809"></a>        record.set(values);
<a name="cl-2810"></a>        record.save(function() {
<a name="cl-2811"></a>            me.loadRecord(record);
<a name="cl-2812"></a>            me.el.unmask();
<a name="cl-2813"></a>            if (!id) {
<a name="cl-2814"></a>                me.fireEvent('created', record);
<a name="cl-2815"></a>            }
<a name="cl-2816"></a>            me.destroy();
<a name="cl-2817"></a>        });
<a name="cl-2818"></a>        
<a name="cl-2819"></a>    },
<a name="cl-2820"></a>    
<a name="cl-2821"></a>    onCancel : function() {
<a name="cl-2822"></a>        this.destroy();
<a name="cl-2823"></a>    },
<a name="cl-2824"></a>    
<a name="cl-2825"></a>    onCreateNew : function() {
<a name="cl-2826"></a>        this.createNewRecord();
<a name="cl-2827"></a>    }
<a name="cl-2828"></a>});
<a name="cl-2829"></a>/**
<a name="cl-2830"></a> * @class C8Y.ux.UserGroupGrid
<a name="cl-2831"></a> * Creates an InventoryGrid
<a name="cl-2832"></a> * @extends Ext.grid.Panel
<a name="cl-2833"></a> */
<a name="cl-2834"></a>Ext.define('C8Y.ux.UserGroupGrid', {
<a name="cl-2835"></a>    extend  : 'Ext.grid.Panel',
<a name="cl-2836"></a>    alias   : 'widget.c8yusergroupgrid',
<a name="cl-2837"></a>    requires: [
<a name="cl-2838"></a>        'Ext.toolbar.Paging',
<a name="cl-2839"></a>        'C8Y.store.UserGroup',
<a name="cl-2840"></a>        'C8Y.ux.UserGroupForm',
<a name="cl-2841"></a>        'Ext.selection.CheckboxModel',
<a name="cl-2842"></a>        'Ext.window.MessageBox'
<a name="cl-2843"></a>    ],
<a name="cl-2844"></a>    mixins  : {
<a name="cl-2845"></a>        feat    : 'C8Y.ux.PanelFeatures'
<a name="cl-2846"></a>    },
<a name="cl-2847"></a>    plugins : [
<a name="cl-2848"></a>        {ptype: 'c8ypanel'}
<a name="cl-2849"></a>    ],
<a name="cl-2850"></a>  
<a name="cl-2851"></a>  	/**
<a name="cl-2852"></a>	 * Automatic Method to initialize the grid, shouldn't be called by the developer
<a name="cl-2853"></a>	 * @method
<a name="cl-2854"></a>	 */
<a name="cl-2855"></a>    initComponent : function() {
<a name="cl-2856"></a>        var me = this;
<a name="cl-2857"></a>
<a name="cl-2858"></a>        this.padding = 20;
<a name="cl-2859"></a>        this.selModel = Ext.create('Ext.selection.CheckboxModel');
<a name="cl-2860"></a>        this.columns = this.buildColumns();
<a name="cl-2861"></a>        this.store = Ext.getStore('c8yusergroup') || Ext.create('C8Y.store.UserGroup', { storeId: 'c8yusergroup', autoLoad: true });
<a name="cl-2862"></a>        this.dockedItems = [this.buildTopActionMenu(this.buildActionMenuItems())];
<a name="cl-2863"></a>        this.title = 'User Group List';
<a name="cl-2864"></a>        
<a name="cl-2865"></a>        //Edit on double click
<a name="cl-2866"></a>        this.on('itemdblclick', function(v, r) { me.detailUserGroup(r); });
<a name="cl-2867"></a>        this.callParent(arguments);
<a name="cl-2868"></a>    },
<a name="cl-2869"></a>    
<a name="cl-2870"></a>    buildColumns : function() {
<a name="cl-2871"></a>        return [
<a name="cl-2872"></a>            { header : 'ID', width: 40, align:'center', dataIndex:'id'},
<a name="cl-2873"></a>            { header : 'Name', width: 200, dataIndex:'name'},
<a name="cl-2874"></a>            { header : 'Roles', flex: 1, renderer: function(val, meta, rec) {
<a name="cl-2875"></a>                var strarr = [];
<a name="cl-2876"></a>                rec.roles().each(function(item) {
<a name="cl-2877"></a>                    strarr.push(item.get('name').replace(/ROLE_/,''));
<a name="cl-2878"></a>                });
<a name="cl-2879"></a>                return strarr.join(', ');
<a name="cl-2880"></a>            }}
<a name="cl-2881"></a>        ];
<a name="cl-2882"></a>    },
<a name="cl-2883"></a>    
<a name="cl-2884"></a>    buildActionMenuItems : function() {
<a name="cl-2885"></a>        var me = this;
<a name="cl-2886"></a>        return [
<a name="cl-2887"></a>                {
<a name="cl-2888"></a>                    text    : 'Add User Group',
<a name="cl-2889"></a>                    handler : this.onAddUserGroup
<a name="cl-2890"></a>                },
<a name="cl-2891"></a>                {
<a name="cl-2892"></a>                    text    : 'Actions',
<a name="cl-2893"></a>                    menu    : {
<a name="cl-2894"></a>                        plain       : true,
<a name="cl-2895"></a>                        defaults    : {'cls': 'action', width:150},
<a name="cl-2896"></a>                        items       : [
<a name="cl-2897"></a>                            { text : 'Edit User Group', scope: this, handler: this.onEditUserGroup },
<a name="cl-2898"></a>                            { text : 'Delete  User Group', scope: this, handler: this.onDeleteUserGroup }
<a name="cl-2899"></a>                        ]
<a name="cl-2900"></a>                    }
<a name="cl-2901"></a>                },
<a name="cl-2902"></a>                {
<a name="cl-2903"></a>                    xtype     : 'textfield',
<a name="cl-2904"></a>                    height    : 24,
<a name="cl-2905"></a>                    cls       : '',
<a name="cl-2906"></a>                    emptyText : 'Search By Name',
<a name="cl-2907"></a>                    itemId    : 'searchField',
<a name="cl-2908"></a>                    listeners : {
<a name="cl-2909"></a>                        specialkey : function(field, e){
<a name="cl-2910"></a>                            if (e.getKey() == e.ENTER) {
<a name="cl-2911"></a>                                me.searchUserGroup(field.getValue());
<a name="cl-2912"></a>                            }
<a name="cl-2913"></a>                        }
<a name="cl-2914"></a>                    }
<a name="cl-2915"></a>                }
<a name="cl-2916"></a>        ];
<a name="cl-2917"></a>    },
<a name="cl-2918"></a>    
<a name="cl-2919"></a>    addUserGroup : function() {
<a name="cl-2920"></a>        this.detailUserGroup();
<a name="cl-2921"></a>    },
<a name="cl-2922"></a>    
<a name="cl-2923"></a>	deleteUserGroups : function(selection) {
<a name="cl-2924"></a>		Ext.Array.each(selection, function(usergroup) {
<a name="cl-2925"></a>            usergroup.destroy();
<a name="cl-2926"></a>        });
<a name="cl-2927"></a>	},
<a name="cl-2928"></a>
<a name="cl-2929"></a>    searchUserGroup : function(searchStr) {
<a name="cl-2930"></a>        var str = this.getStore(),
<a name="cl-2931"></a>            me = this;
<a name="cl-2932"></a>        this.getComponent('topDock').add({
<a name="cl-2933"></a>            xtype   : 'button',
<a name="cl-2934"></a>            text    : 'Remove Filter',
<a name="cl-2935"></a>            itemId  : 'removeFilter',
<a name="cl-2936"></a>            scale   : 'small',
<a name="cl-2937"></a>            scope   : this,
<a name="cl-2938"></a>            handler : function() {
<a name="cl-2939"></a>                str.clearFilter();
<a name="cl-2940"></a>                me.getComponent('topDock').getComponent('removeFilter').destroy();
<a name="cl-2941"></a>                me.getComponent('topDock').getComponent('searchField').setValue('');
<a name="cl-2942"></a>            }
<a name="cl-2943"></a>        });
<a name="cl-2944"></a>        str.clearFilter();
<a name="cl-2945"></a>        str.filterBy(function(record) {
<a name="cl-2946"></a>            var rg = new RegExp(searchStr,'i'),
<a name="cl-2947"></a>                groupName = record.get('name');
<a name="cl-2948"></a>            return (groupName &amp;&amp; groupName.match(rg));
<a name="cl-2949"></a>        });
<a name="cl-2950"></a>    },
<a name="cl-2951"></a>
<a name="cl-2952"></a>    detailUserGroup : function(user) {
<a name="cl-2953"></a>        var title = user ? "Edit User Group " + user.get('name') : 'Create new User Group',
<a name="cl-2954"></a>            store = this.getStore(),
<a name="cl-2955"></a>            form = Ext.create('C8Y.ux.UserGroupForm', {
<a name="cl-2956"></a>                windowed: true,
<a name="cl-2957"></a>                title   : title,
<a name="cl-2958"></a>                listeners : {
<a name="cl-2959"></a>                    'render' : function(form) {
<a name="cl-2960"></a>                        if (user) {
<a name="cl-2961"></a>                            form.loadUserGroup(user);
<a name="cl-2962"></a>                        } else {
<a name="cl-2963"></a>                            form.createNewRecord();
<a name="cl-2964"></a>                        }
<a name="cl-2965"></a>                    },
<a name="cl-2966"></a>                    'created' : function(rec) {
<a name="cl-2967"></a>                        store.add(rec);
<a name="cl-2968"></a>                    }
<a name="cl-2969"></a>                }
<a name="cl-2970"></a>            });
<a name="cl-2971"></a>        return form;
<a name="cl-2972"></a>    },
<a name="cl-2973"></a>
<a name="cl-2974"></a>    onAddUserGroup : function() {
<a name="cl-2975"></a>        this.addUserGroup();
<a name="cl-2976"></a>    },
<a name="cl-2977"></a>
<a name="cl-2978"></a>	onDeleteUserGroup : function() {
<a name="cl-2979"></a>        var sel = this.getSelectionModel().getSelection(),
<a name="cl-2980"></a>             qt = sel.length,
<a name="cl-2981"></a>             me = this;
<a name="cl-2982"></a>         if (qt) {
<a name="cl-2983"></a>             Ext.Msg.confirm(
<a name="cl-2984"></a>                'Delete users',
<a name="cl-2985"></a>                Ext.String.format('Do you want to delete {0} user groups{1}?', qt, (qt &gt; 1 ? 's':'')),
<a name="cl-2986"></a>                function(btn) {
<a name="cl-2987"></a>                    if (btn == 'yes') {
<a name="cl-2988"></a>                        me.deleteUserGroups(sel);
<a name="cl-2989"></a>                    }
<a name="cl-2990"></a>                }
<a name="cl-2991"></a>             );
<a name="cl-2992"></a>         }
<a name="cl-2993"></a>    },
<a name="cl-2994"></a>
<a name="cl-2995"></a>	onEditUserGroup : function() {
<a name="cl-2996"></a>        var sel = this.getSelectionModel().getSelection(),
<a name="cl-2997"></a>            selRecord = sel.shift();
<a name="cl-2998"></a>        
<a name="cl-2999"></a>        if (selRecord) {
<a name="cl-3000"></a>            this.detailUserGroup(selRecord);
<a name="cl-3001"></a>        }
<a name="cl-3002"></a>    }
<a name="cl-3003"></a>});
<a name="cl-3004"></a>Ext.define('C8Y.ux.UserManagementPanel', {
<a name="cl-3005"></a>    extend      : 'Ext.panel.Panel',
<a name="cl-3006"></a>    alias       : 'widget.c8yusermanagementpanel',
<a name="cl-3007"></a>    requires    : [
<a name="cl-3008"></a>        'C8Y.model.Navigation',
<a name="cl-3009"></a>        'C8Y.ux.UserGrid',
<a name="cl-3010"></a>        'C8Y.ux.UserGroupGrid',
<a name="cl-3011"></a>        'C8Y.ux.UserRoleGrid',
<a name="cl-3012"></a>        'Ext.view.View'
<a name="cl-3013"></a>    ],
<a name="cl-3014"></a>    
<a name="cl-3015"></a>    initComponent : function() {
<a name="cl-3016"></a>		var userRoleStore = Ext.create('C8Y.store.UserRole', {storeId: 'c8yuserrole', autoLoad: true});
<a name="cl-3017"></a>
<a name="cl-3018"></a>        this.layout = 'border';
<a name="cl-3019"></a>        this.items = this.buildItems();
<a name="cl-3020"></a>        this.bodyStyle  = 'background:transparent';
<a name="cl-3021"></a>        this.callParent(arguments);
<a name="cl-3022"></a>		userRoleStore.sort('name', 'ASC');
<a name="cl-3023"></a>		Ext.create('C8Y.store.UserGroup', { storeId: 'c8yusergroup', autoLoad: true });
<a name="cl-3024"></a>    },
<a name="cl-3025"></a>    
<a name="cl-3026"></a>    buildItems : function() {
<a name="cl-3027"></a>        var items = [
<a name="cl-3028"></a>            Ext.apply(this.buildNavigation(), {region:'west', width:250, split: true}),
<a name="cl-3029"></a>            {
<a name="cl-3030"></a>                xtype   : 'container',
<a name="cl-3031"></a>                layout  : 'fit',
<a name="cl-3032"></a>                region  : 'center',
<a name="cl-3033"></a>                itemId  : 'centerItem'
<a name="cl-3034"></a>            }
<a name="cl-3035"></a>        ];
<a name="cl-3036"></a>        
<a name="cl-3037"></a>        return items;
<a name="cl-3038"></a>    },
<a name="cl-3039"></a>    
<a name="cl-3040"></a>    buildNavigation : function() {
<a name="cl-3041"></a>        var navigation =  [
<a name="cl-3042"></a>            // { id : '', name: 'Recent Activity', isTitle : true},
<a name="cl-3043"></a>            { id : '', name: 'User Management', isTitle : true},
<a name="cl-3044"></a>            { id : 'users', name: 'Users'},
<a name="cl-3045"></a>            { id : 'groups', name: 'Groups'},
<a name="cl-3046"></a>            { id : 'roles', name: 'Roles'}
<a name="cl-3047"></a>            // { id : '', name: 'Other Management', isTitle : true}
<a name="cl-3048"></a>        ];
<a name="cl-3049"></a>        return  {
<a name="cl-3050"></a>            xtype       : 'dataview',
<a name="cl-3051"></a>            store       : Ext.create('Ext.data.Store', {
<a name="cl-3052"></a>                model   : 'C8Y.model.Navigation',
<a name="cl-3053"></a>                data    : navigation
<a name="cl-3054"></a>            }),
<a name="cl-3055"></a>            trackOver   : true,
<a name="cl-3056"></a>            cls         : 'nav-list',
<a name="cl-3057"></a>            itemSelector: '.nav-list-item',
<a name="cl-3058"></a>            itemId      : 'nav',
<a name="cl-3059"></a>            overItemCls : 'nav-list-item-hover',
<a name="cl-3060"></a>            tpl         : '&lt;tpl for="."&gt;&lt;div class="nav-list-item&lt;tpl if="isTitle"&gt; title&lt;/tpl&gt;&lt;tpl if="active"&gt; active&lt;/tpl&gt;"&gt;{name}&lt;/div&gt;&lt;/tpl&gt;',
<a name="cl-3061"></a>            singleSelect: true,
<a name="cl-3062"></a>            listeners          : {
<a name="cl-3063"></a>                scope          : this,
<a name="cl-3064"></a>                itemclick      : this.onClick,
<a name="cl-3065"></a>                render         : function(dv) {
<a name="cl-3066"></a>                    var store = dv.getStore(),
<a name="cl-3067"></a>                        frec;
<a name="cl-3068"></a>                    
<a name="cl-3069"></a>                    store.each(function(rec) {
<a name="cl-3070"></a>                        if (!rec.get('isTitle')) {
<a name="cl-3071"></a>                            frec = rec;
<a name="cl-3072"></a>                            return false;
<a name="cl-3073"></a>                        }
<a name="cl-3074"></a>                    });
<a name="cl-3075"></a>                    frec.set('active', true);
<a name="cl-3076"></a>                    this.changeMainItem(frec.get('id'));
<a name="cl-3077"></a>                }
<a name="cl-3078"></a>            },
<a name="cl-3079"></a>            style : "border: 1px solid #B3B3B3;background:#F2F2F2;margin:10px 0 0 0; border-left:none;border-bottom:none;"
<a name="cl-3080"></a>        };
<a name="cl-3081"></a>    },
<a name="cl-3082"></a>    
<a name="cl-3083"></a>    onClick : function(view, model) {
<a name="cl-3084"></a>        var id = model.get('id');
<a name="cl-3085"></a>        if (id &amp;&amp; !model.get('isTitle')) {
<a name="cl-3086"></a>            this.changeMainItem(id);
<a name="cl-3087"></a>            //TODO: MUST REDO THIS WHEN EXTRACT TO CLASS
<a name="cl-3088"></a>            var nav = this.getComponent('nav'),
<a name="cl-3089"></a>                store = nav.getStore();
<a name="cl-3090"></a>            store.each(function(rec) {
<a name="cl-3091"></a>                rec.set('active', rec == model);
<a name="cl-3092"></a>            });
<a name="cl-3093"></a>            
<a name="cl-3094"></a>        }
<a name="cl-3095"></a>    },
<a name="cl-3096"></a>    
<a name="cl-3097"></a>    changeMainItem : function(id) {
<a name="cl-3098"></a>        var items = {
<a name="cl-3099"></a>                'users' : { xtype : 'c8yusergrid'},
<a name="cl-3100"></a>                'groups': { xtype : 'c8yusergroupgrid'},
<a name="cl-3101"></a>                'roles' : { xtype : 'c8yuserrolegrid'}
<a name="cl-3102"></a>            },
<a name="cl-3103"></a>            newCenter = items[id],
<a name="cl-3104"></a>            currentCenter = this.getComponent('centerItem');
<a name="cl-3105"></a>        currentCenter.removeAll(true);
<a name="cl-3106"></a>        currentCenter.add(newCenter);
<a name="cl-3107"></a>    } 
<a name="cl-3108"></a>});
<a name="cl-3109"></a>/**
<a name="cl-3110"></a>* @class C8Y.ux.InventoryTree
<a name="cl-3111"></a>* Inventory Tree component witch can be configured with a Managed Object root node
<a name="cl-3112"></a>* @extends Ext.tree.Panel
<a name="cl-3113"></a>* @alias c8yinventorytree
<a name="cl-3114"></a>*/
<a name="cl-3115"></a>Ext.define( 'C8Y.ux.InventoryTree',{
<a name="cl-3116"></a>    extend  : 'Ext.tree.Panel',
<a name="cl-3117"></a>    
<a name="cl-3118"></a>    alias   : 'widget.c8yinventorytree',
<a name="cl-3119"></a>    
<a name="cl-3120"></a>    requires : [
<a name="cl-3121"></a>        'C8Y.model.ManagedObject',
<a name="cl-3122"></a>        'Ext.data.TreeStore',
<a name="cl-3123"></a>        'Ext.PluginManager',
<a name="cl-3124"></a>        'Ext.tree.plugin.TreeViewDragDrop',
<a name="cl-3125"></a>        'Ext.tree.ViewDragZone',
<a name="cl-3126"></a>        'Ext.tree.ViewDropZone'
<a name="cl-3127"></a>    ],
<a name="cl-3128"></a>
<a name="cl-3129"></a>    config : {
<a name="cl-3130"></a>        childType  : 'childAssets',
<a name="cl-3131"></a>    	editable   : false,
<a name="cl-3132"></a>		detail	   : false
<a name="cl-3133"></a>    },
<a name="cl-3134"></a>    
<a name="cl-3135"></a>    initComponent : function() {
<a name="cl-3136"></a>        var me = this;
<a name="cl-3137"></a>        
<a name="cl-3138"></a>        this.useArrows = true;
<a name="cl-3139"></a>        this.listeners = {
<a name="cl-3140"></a>            scope 		: this,
<a name="cl-3141"></a>            itemexpand	: this.onExpand
<a name="cl-3142"></a>        };
<a name="cl-3143"></a>		this.viewConfig = {
<a name="cl-3144"></a>            listeners   : {
<a name="cl-3145"></a>                scope   : this
<a name="cl-3146"></a>            }
<a name="cl-3147"></a>        };
<a name="cl-3148"></a>        this.editable = this.editable	|| this.config.editable;
<a name="cl-3149"></a>        this.childType = this.childType || this.config.childType;
<a name="cl-3150"></a>        this.detail = this.detail || this.config.detail;
<a name="cl-3151"></a>
<a name="cl-3152"></a>        this.fnmap = {
<a name="cl-3153"></a>            'childDevices' : 'Devices',
<a name="cl-3154"></a>            'childAssets'  : 'Assets'
<a name="cl-3155"></a>        };
<a name="cl-3156"></a>		
<a name="cl-3157"></a>		if (this.editable) {
<a name="cl-3158"></a>			this.listeners.itemcontextmenu = this.onContextMenu;
<a name="cl-3159"></a>			this.viewConfig.listeners.render = this.initializeDrop;
<a name="cl-3160"></a>			this.buildContextMenu();
<a name="cl-3161"></a>		}
<a name="cl-3162"></a>		
<a name="cl-3163"></a>		if (!this.detail) {
<a name="cl-3164"></a>			this.root = {
<a name="cl-3165"></a>				expanded : true,
<a name="cl-3166"></a>				children : []
<a name="cl-3167"></a>			};
<a name="cl-3168"></a>			this.datastore =  Ext.create(
<a name="cl-3169"></a>				'C8Y.store.ManagedObject', 
<a name="cl-3170"></a>				{ 
<a name="cl-3171"></a>					proxy 		: C8Y.client.inventory.getProxy(this.managedObjectType),
<a name="cl-3172"></a>					storeId 	: 'c8yinventorystore' 
<a name="cl-3173"></a>				}
<a name="cl-3174"></a>			);
<a name="cl-3175"></a>			this.datastore.on('load', this.onLoadStore, this);
<a name="cl-3176"></a>			this.on('render', function() {
<a name="cl-3177"></a>				this.datastore.load();
<a name="cl-3178"></a>			}, this, {single:true});
<a name="cl-3179"></a>		}
<a name="cl-3180"></a>        this.rootVisible = this.detail;
<a name="cl-3181"></a>		this.on('select', function(sm, m) {
<a name="cl-3182"></a>			me.fireEvent('selectmo', m.get('data'));
<a name="cl-3183"></a>		});
<a name="cl-3184"></a>
<a name="cl-3185"></a>        this.callParent();
<a name="cl-3186"></a>    },
<a name="cl-3187"></a>
<a name="cl-3188"></a>    initializeDrop : function(v) {
<a name="cl-3189"></a>        var me = this;
<a name="cl-3190"></a>
<a name="cl-3191"></a>        this.dropZone = Ext.create('Ext.dd.DropZone', me.getEl(), {
<a name="cl-3192"></a>
<a name="cl-3193"></a>            getTargetFromEvent: function(e) {
<a name="cl-3194"></a>                return e.getTarget('.x-grid-row');
<a name="cl-3195"></a>            },
<a name="cl-3196"></a>
<a name="cl-3197"></a>            //      On entry into a target node, highlight that node.
<a name="cl-3198"></a>            onNodeEnter : function(target, dd, e, data) {
<a name="cl-3199"></a>                  Ext.fly(target).setStyle('background-color:green;');
<a name="cl-3200"></a>            },
<a name="cl-3201"></a>
<a name="cl-3202"></a>            // On exit from a target node, unhighlight that node.
<a name="cl-3203"></a>            onNodeOut : function(target, dd, e, data){
<a name="cl-3204"></a>                   Ext.fly(target).setStyle('background-color:inherit;');
<a name="cl-3205"></a>            },
<a name="cl-3206"></a>            //      While over a target node, return the default drop allowed class which
<a name="cl-3207"></a>            //      places a "tick" icon into the drag proxy.
<a name="cl-3208"></a>            onNodeOver : function(target, dd, e, data){
<a name="cl-3209"></a>                    var rec = data.rec,
<a name="cl-3210"></a>                        dropRec = me.getView().getRecord(target).get('data'),
<a name="cl-3211"></a>                        dropAllowed = rec.get('id') != dropRec.get('id');
<a name="cl-3212"></a>
<a name="cl-3213"></a>                    return (dropAllowed &amp;&amp; Ext.dd.DropZone.prototype.dropAllowed);
<a name="cl-3214"></a>            },
<a name="cl-3215"></a>
<a name="cl-3216"></a>            onNodeDrop : function(target, dd, e, data){
<a name="cl-3217"></a>                var rowBody = Ext.fly(target).findParent('.x-grid-row', null, false),
<a name="cl-3218"></a>                    node = me.getView().getRecord(rowBody),
<a name="cl-3219"></a>                    rec = node.get('data'),
<a name="cl-3220"></a>                    childFn = me.childType,
<a name="cl-3221"></a>                    addFn = 'add'+me.fnmap[childFn];
<a name="cl-3222"></a>
<a name="cl-3223"></a>                rec[addFn](data.rec, function() { 
<a name="cl-3224"></a>                    me.loadNode(null, node, true);
<a name="cl-3225"></a>                });
<a name="cl-3226"></a>                return true;
<a name="cl-3227"></a>            }
<a name="cl-3228"></a>        });
<a name="cl-3229"></a>    },
<a name="cl-3230"></a>
<a name="cl-3231"></a>    onExpand : function(node) {
<a name="cl-3232"></a>        this.loadNode(node.get('data'), node);  
<a name="cl-3233"></a>    },
<a name="cl-3234"></a>
<a name="cl-3235"></a>    onContextMenu : function(view, node, el, index, e) {
<a name="cl-3236"></a>        this.rightClickNode = node;
<a name="cl-3237"></a>        e.stopEvent();
<a name="cl-3238"></a>        this.contextMenu.showAt(e.getXY());
<a name="cl-3239"></a>        return false;
<a name="cl-3240"></a>    },
<a name="cl-3241"></a>
<a name="cl-3242"></a>    onRemoveChild : function() {
<a name="cl-3243"></a>        var mo = this.rightClickNode.get('data'),
<a name="cl-3244"></a>            parentNode = this.rightClickNode.parentNode,
<a name="cl-3245"></a>            moParent =  parentNode.get('data'),
<a name="cl-3246"></a>            me = this,
<a name="cl-3247"></a>            removeChildFn = 'remove'+this.fnmap[this.childType];
<a name="cl-3248"></a>
<a name="cl-3249"></a>        if (mo &amp;&amp; moParent) {
<a name="cl-3250"></a>            moParent[removeChildFn](mo, function() {
<a name="cl-3251"></a>                me.loadNode(null, parentNode);  
<a name="cl-3252"></a>            });
<a name="cl-3253"></a>        }
<a name="cl-3254"></a>    },
<a name="cl-3255"></a>
<a name="cl-3256"></a>	onLoadStore : function(store) {
<a name="cl-3257"></a>		var rootNode = this.getRootNode(),
<a name="cl-3258"></a>			clsName = Ext.getClassName(rootNode),
<a name="cl-3259"></a>			children = [],
<a name="cl-3260"></a>			me = this;
<a name="cl-3261"></a>		store.each(function(item) {
<a name="cl-3262"></a>			children.push(Ext.create(clsName, me.buildNodeObj(item)));
<a name="cl-3263"></a>		});
<a name="cl-3264"></a>		rootNode.removeAll();
<a name="cl-3265"></a>		rootNode.appendChild(children);
<a name="cl-3266"></a>	},
<a name="cl-3267"></a>
<a name="cl-3268"></a>    loadNode : function(rec, node, forceexpand) {
<a name="cl-3269"></a>        var mo = rec || node.get('data'),
<a name="cl-3270"></a>            childFn = this.childType,
<a name="cl-3271"></a>            str = mo[childFn](),
<a name="cl-3272"></a>            me = this,
<a name="cl-3273"></a>            loadFn = 'load'+this.fnmap[childFn];
<a name="cl-3274"></a>        
<a name="cl-3275"></a>        if (mo) {
<a name="cl-3276"></a>            mo[loadFn](function() {
<a name="cl-3277"></a>            var children = [],
<a name="cl-3278"></a>                rootNode = node || me.getRootNode(),
<a name="cl-3279"></a>                className = Ext.getClassName(rootNode);
<a name="cl-3280"></a>            
<a name="cl-3281"></a>            rootNode.removeAll();
<a name="cl-3282"></a>            str.each(function(item) {
<a name="cl-3283"></a>                children.push(Ext.create(className, me.buildNodeObj(item)));
<a name="cl-3284"></a>            });
<a name="cl-3285"></a>            rootNode.appendChild(children);
<a name="cl-3286"></a>            if (!node || forceexpand) rootNode.expand();
<a name="cl-3287"></a>            });
<a name="cl-3288"></a>        }
<a name="cl-3289"></a>    },
<a name="cl-3290"></a>
<a name="cl-3291"></a>    buildNodeObj : function(mo) {
<a name="cl-3292"></a>        var childFn = this.childType,
<a name="cl-3293"></a>            hasChildFn = 'has'+this.fnmap[childFn],
<a name="cl-3294"></a>            // iconCls = mo[hasChildFn]() ? 'iconManagedObjectWithChildren' : 'iconManagedObject';
<a name="cl-3295"></a>            iconCls = 'iconManagedObject';
<a name="cl-3296"></a>
<a name="cl-3297"></a>        return {
<a name="cl-3298"></a>            text    : mo.get('name'),
<a name="cl-3299"></a>            leaf    : false,
<a name="cl-3300"></a>            iconCls : iconCls,
<a name="cl-3301"></a>            data    : mo
<a name="cl-3302"></a>        };   
<a name="cl-3303"></a>    },
<a name="cl-3304"></a>
<a name="cl-3305"></a>    buildContextMenu : function(mo) {
<a name="cl-3306"></a>        this.contextMenu = Ext.create('Ext.menu.Menu', {
<a name="cl-3307"></a>            items: [{
<a name="cl-3308"></a>                text  : 'Remove child',
<a name="cl-3309"></a>                scope : this, 
<a name="cl-3310"></a>                handler: this.onRemoveChild
<a name="cl-3311"></a>            }]
<a name="cl-3312"></a>        });    
<a name="cl-3313"></a>    },
<a name="cl-3314"></a>
<a name="cl-3315"></a>    setManageObjectRoot : function(mo) {
<a name="cl-3316"></a>        this.setRootNode(this.buildNodeObj(mo));
<a name="cl-3317"></a>        this.getRootNode().expand();
<a name="cl-3318"></a>    }
<a name="cl-3319"></a>    
<a name="cl-3320"></a>});
<a name="cl-3321"></a>Ext.define('C8Y.ux.InventoryManagementPanel', {
<a name="cl-3322"></a>    extend  : 'Ext.panel.Panel',
<a name="cl-3323"></a>    alias   : 'widget.c8yinventorymanagement',
<a name="cl-3324"></a>
<a name="cl-3325"></a>    requires: [
<a name="cl-3326"></a>        'C8Y.ux.InventoryGrid',
<a name="cl-3327"></a>        'C8Y.ux.InventoryTree',
<a name="cl-3328"></a>        'C8Y.ux.ManagedObjectForm',
<a name="cl-3329"></a>        'Ext.layout.container.Table',
<a name="cl-3330"></a>        'Ext.layout.container.Border',
<a name="cl-3331"></a>        'Ext.layout.container.VBox'
<a name="cl-3332"></a>    ],
<a name="cl-3333"></a>    
<a name="cl-3334"></a>    initComponent : function() {
<a name="cl-3335"></a>        this.layout = "border";
<a name="cl-3336"></a>        this.bodyStyle  = 'background:transparent';
<a name="cl-3337"></a>        this.border = 0;
<a name="cl-3338"></a>        this.items = [
<a name="cl-3339"></a>            {
<a name="cl-3340"></a>                xtype  : 'c8yinventorygrid',
<a name="cl-3341"></a>                width  : 400,
<a name="cl-3342"></a>                listeners : {
<a name="cl-3343"></a>                    scope   : this,
<a name="cl-3344"></a>                    itemdblclick  : this.onDblClick,
<a name="cl-3345"></a>                    createnew     : this.onCreateNew
<a name="cl-3346"></a>                },
<a name="cl-3347"></a>                region  : 'west'
<a name="cl-3348"></a>            },
<a name="cl-3349"></a>            {
<a name="cl-3350"></a>                xtype       : 'panel',
<a name="cl-3351"></a>                region      : 'center',
<a name="cl-3352"></a>                border      : 0,
<a name="cl-3353"></a>                //Dirty hack.. i'm tired ok?
<a name="cl-3354"></a>                bodyStyle    : 'border-top-width: 0 !important;',
<a name="cl-3355"></a>                plugins     : [
<a name="cl-3356"></a>                    { ptype: 'c8ypanel'}
<a name="cl-3357"></a>                ],
<a name="cl-3358"></a>                title       : 'Managed Object Detail',
<a name="cl-3359"></a>                layout: {
<a name="cl-3360"></a>                    type    : 'hbox',
<a name="cl-3361"></a>                    align   : 'stretch'
<a name="cl-3362"></a>                },
<a name="cl-3363"></a>                items    : [
<a name="cl-3364"></a>                    {
<a name="cl-3365"></a>                        xtype    : 'c8ymanagedobjectform',
<a name="cl-3366"></a>                        itemId   : 'mainproperty',
<a name="cl-3367"></a>                        //Dirty hack.. i'm tired ok?
<a name="cl-3368"></a>                        bodyStyle    : 'border-top-width: 0 !important;',
<a name="cl-3369"></a>                        border   : false,
<a name="cl-3370"></a>                        autoScroll: true,
<a name="cl-3371"></a>                        flex     : 1
<a name="cl-3372"></a>                    },
<a name="cl-3373"></a>                    {
<a name="cl-3374"></a>                        xtype    : 'container',
<a name="cl-3375"></a>                        width    : 200,
<a name="cl-3376"></a>                        border   : false,
<a name="cl-3377"></a>                        layout   : {
<a name="cl-3378"></a>                            type    : 'vbox',
<a name="cl-3379"></a>                            align   : 'stretch'
<a name="cl-3380"></a>                        },
<a name="cl-3381"></a>                        items    : [
<a name="cl-3382"></a>                            {
<a name="cl-3383"></a>                                xtype   : 'c8yinventorytree',
<a name="cl-3384"></a>                                editable: true,
<a name="cl-3385"></a>                                title   : 'Child Assets',
<a name="cl-3386"></a>                                detail  : true,
<a name="cl-3387"></a>                                flex    : 1,
<a name="cl-3388"></a>                                margins : '18px 0 20px 0'
<a name="cl-3389"></a>                            },
<a name="cl-3390"></a>                            {
<a name="cl-3391"></a>                                xtype   : 'c8yinventorytree',
<a name="cl-3392"></a>                                title   : 'Child Devices',
<a name="cl-3393"></a>                                editable: true,
<a name="cl-3394"></a>                                childType: 'childDevices',
<a name="cl-3395"></a>                                detail  : true,
<a name="cl-3396"></a>                                flex    : 1
<a name="cl-3397"></a>                            }
<a name="cl-3398"></a>                        ]
<a name="cl-3399"></a>                    }
<a name="cl-3400"></a>                    
<a name="cl-3401"></a>                ]
<a name="cl-3402"></a>            }
<a name="cl-3403"></a>        ];
<a name="cl-3404"></a>        this.callParent();  
<a name="cl-3405"></a>    },
<a name="cl-3406"></a>
<a name="cl-3407"></a>    onDblClick : function(view, model) {
<a name="cl-3408"></a>        var pform = this.query('#mainproperty').shift(),
<a name="cl-3409"></a>            trees = this.query('c8yinventorytree');
<a name="cl-3410"></a>        if (pform) {
<a name="cl-3411"></a>            pform.loadManagedObject(model);
<a name="cl-3412"></a>        }
<a name="cl-3413"></a>
<a name="cl-3414"></a>        Ext.Array.each(trees, function(tree) {
<a name="cl-3415"></a>            tree.setManageObjectRoot(model);
<a name="cl-3416"></a>        });
<a name="cl-3417"></a>    },
<a name="cl-3418"></a>
<a name="cl-3419"></a>    onCreateNew : function() {
<a name="cl-3420"></a>        var pgrid = this.query('#mainproperty').shift();
<a name="cl-3421"></a>        if (pgrid) {
<a name="cl-3422"></a>            pgrid.createNewRecord();
<a name="cl-3423"></a>        }   
<a name="cl-3424"></a>    }
<a name="cl-3425"></a>});
<a name="cl-3426"></a>Ext.define('C8Y.ux.MeasurementGraph', {
<a name="cl-3427"></a>    extend   : 'Ext.panel.Panel',
<a name="cl-3428"></a>    alias    : 'widget.c8ymeasurementgraph', 
<a name="cl-3429"></a>    requires : [
<a name="cl-3430"></a>        'Ext.chart.Chart',
<a name="cl-3431"></a>        'Ext.chart.axis.Time',
<a name="cl-3432"></a>        'Ext.chart.axis.Numeric',
<a name="cl-3433"></a>        'Ext.chart.axis.Category',
<a name="cl-3434"></a>        'Ext.chart.theme.Base',
<a name="cl-3435"></a>        'Ext.chart.theme.Theme',
<a name="cl-3436"></a>        'Ext.chart.series.Line',
<a name="cl-3437"></a>        'Ext.chart.series.Scatter',
<a name="cl-3438"></a>        'Ext.chart.series.Area',
<a name="cl-3439"></a>        'Ext.chart.series.Bar',
<a name="cl-3440"></a>        'Ext.form.field.Date'
<a name="cl-3441"></a>    ],
<a name="cl-3442"></a>    mixins  : {
<a name="cl-3443"></a>        feat    : 'C8Y.ux.PanelFeatures'
<a name="cl-3444"></a>    },
<a name="cl-3445"></a>    layout : 'fit',
<a name="cl-3446"></a>    
<a name="cl-3447"></a>    initComponent : function() {
<a name="cl-3448"></a>        if (!this.measurementProperty) {
<a name="cl-3449"></a>            throw('measurementProperty not defined');
<a name="cl-3450"></a>        }
<a name="cl-3451"></a>        if (!this.series) {
<a name="cl-3452"></a>            throw('series not defined');
<a name="cl-3453"></a>        } else {
<a name="cl-3454"></a>            this.series = this.buildSeries();
<a name="cl-3455"></a>        }
<a name="cl-3456"></a>        this.defaultLoadCfg = {
<a name="cl-3457"></a>            dateFrom : Ext.Date.add((new Date()), Ext.Date.DAY, -7),
<a name="cl-3458"></a>            dateTo   : (new Date())
<a name="cl-3459"></a>        };
<a name="cl-3460"></a>        this.border = false;
<a name="cl-3461"></a>        this.store = this.buildStore();
<a name="cl-3462"></a>        this.dockedItems =  this.dockedItems || [];
<a name="cl-3463"></a>        if (!Ext.isArray(this.dockedItems)) {
<a name="cl-3464"></a>            this.dockedItems = [this.dockedItems];
<a name="cl-3465"></a>        }
<a name="cl-3466"></a>        this.dockedItems.push(this.buildDockedItems());
<a name="cl-3467"></a>        this.items = this.buildItems();
<a name="cl-3468"></a>        this.intervalType = 'week';
<a name="cl-3469"></a>        this.on('render', function() {
<a name="cl-3470"></a>            this.setTimeInterval();
<a name="cl-3471"></a>        }, this, {single:true});
<a name="cl-3472"></a>        this.callParent();
<a name="cl-3473"></a>    },
<a name="cl-3474"></a>    
<a name="cl-3475"></a>    buildItems : function() {
<a name="cl-3476"></a>        return {
<a name="cl-3477"></a>            xtype   : 'chart',
<a name="cl-3478"></a>            axes    : this.buildAxes(),
<a name="cl-3479"></a>            series  : this.series,
<a name="cl-3480"></a>            store   : this.store,
<a name="cl-3481"></a>            legend: {
<a name="cl-3482"></a>                position: 'right'
<a name="cl-3483"></a>            }
<a name="cl-3484"></a>        };
<a name="cl-3485"></a>    },
<a name="cl-3486"></a>    
<a name="cl-3487"></a>    buildDockedItems : function() {
<a name="cl-3488"></a>        var id = Ext.id();
<a name="cl-3489"></a>        return this.buildTopActionMenu([
<a name="cl-3490"></a>                {
<a name="cl-3491"></a>                    xtype       : 'datefield',
<a name="cl-3492"></a>                    itemId      : 'dateFrom',
<a name="cl-3493"></a>                    maxValue    : new Date(),
<a name="cl-3494"></a>                    cls         : '',
<a name="cl-3495"></a>                    fieldLabel  : 'From',
<a name="cl-3496"></a>                    labelWidth  : 35,
<a name="cl-3497"></a>                    value       : this.defaultLoadCfg.dateFrom,
<a name="cl-3498"></a>                    listeners   : {
<a name="cl-3499"></a>                        scope   : this,
<a name="cl-3500"></a>                        change  : function() {
<a name="cl-3501"></a>                            this.setTimeInterval();
<a name="cl-3502"></a>                        }
<a name="cl-3503"></a>                    }
<a name="cl-3504"></a>                },
<a name="cl-3505"></a>                {
<a name="cl-3506"></a>                    xtype       : 'datefield',
<a name="cl-3507"></a>                    itemId      : 'dateTo',
<a name="cl-3508"></a>                    cls         : '',
<a name="cl-3509"></a>                    maxValue    : new Date(),
<a name="cl-3510"></a>                    fieldLabel  : 'To',
<a name="cl-3511"></a>                    labelWidth  : 15
<a name="cl-3512"></a>                },
<a name="cl-3513"></a>                {
<a name="cl-3514"></a>                    text        : 'Day',
<a name="cl-3515"></a>                    scope       : this,
<a name="cl-3516"></a>                    toggleGroup : 'c8ygraph' + id,
<a name="cl-3517"></a>                    handler     : function() {
<a name="cl-3518"></a>                        this.intervalType = 'day';
<a name="cl-3519"></a>                        this.setTimeInterval();
<a name="cl-3520"></a>                    }
<a name="cl-3521"></a>                },
<a name="cl-3522"></a>                {
<a name="cl-3523"></a>                    text        : 'Week',
<a name="cl-3524"></a>                    scope       : this,
<a name="cl-3525"></a>                    pressed     : true,
<a name="cl-3526"></a>                    toggleGroup : 'c8ygraph' + id,
<a name="cl-3527"></a>                    handler     : function() {
<a name="cl-3528"></a>                        this.intervalType = 'week';
<a name="cl-3529"></a>                        this.setTimeInterval();
<a name="cl-3530"></a>                    }
<a name="cl-3531"></a>                },
<a name="cl-3532"></a>                {
<a name="cl-3533"></a>                    text        : 'Month',
<a name="cl-3534"></a>                    scope       : this,
<a name="cl-3535"></a>                    toggleGroup : 'c8ygraph' + id,
<a name="cl-3536"></a>                    handler     : function() {
<a name="cl-3537"></a>                        this.intervalType = 'month';
<a name="cl-3538"></a>                        this.setTimeInterval();
<a name="cl-3539"></a>                    }
<a name="cl-3540"></a>                },
<a name="cl-3541"></a>                {
<a name="cl-3542"></a>                    text        : 'Custom',
<a name="cl-3543"></a>                    scope       : this,
<a name="cl-3544"></a>                    toggleGroup : 'c8ygraph' + id,
<a name="cl-3545"></a>                    handler     : function() {
<a name="cl-3546"></a>                        this.intervalType = 'custom';
<a name="cl-3547"></a>                        this.setTimeInterval();
<a name="cl-3548"></a>                    }
<a name="cl-3549"></a>                },
<a name="cl-3550"></a>                {
<a name="cl-3551"></a>                    xtype       : 'container',
<a name="cl-3552"></a>                    flex        : 1
<a name="cl-3553"></a>                },
<a name="cl-3554"></a>                {
<a name="cl-3555"></a>                    scope       : this,
<a name="cl-3556"></a>                    iconCls     : 'iconRefresh',
<a name="cl-3557"></a>                    handler     : this.onDateChange
<a name="cl-3558"></a>                }
<a name="cl-3559"></a>        ]);
<a name="cl-3560"></a>    },
<a name="cl-3561"></a>    
<a name="cl-3562"></a>    buildAxes : function() {
<a name="cl-3563"></a>        return [
<a name="cl-3564"></a>            {
<a name="cl-3565"></a>                title       : 'Time',
<a name="cl-3566"></a>                type        : 'Category',
<a name="cl-3567"></a>                position    : 'bottom',
<a name="cl-3568"></a>                fields      : ['time'],
<a name="cl-3569"></a>                dateFormat  : 'd M G:i',
<a name="cl-3570"></a>                grid        : true
<a name="cl-3571"></a>            },
<a name="cl-3572"></a>            {
<a name="cl-3573"></a>                title       : this.ytitle,
<a name="cl-3574"></a>                type        : 'Numeric',
<a name="cl-3575"></a>                position    : 'left',
<a name="cl-3576"></a>                mininum     : 0,
<a name="cl-3577"></a>                fields      : this.getFieldList(),
<a name="cl-3578"></a>                grid        : true
<a name="cl-3579"></a>            }
<a name="cl-3580"></a>        ];
<a name="cl-3581"></a>    },
<a name="cl-3582"></a>    
<a name="cl-3583"></a>    buildSeries : function() {
<a name="cl-3584"></a>        this.series = Ext.Array.map(this.series, function(serie) {
<a name="cl-3585"></a>            serie.xField = 'time';
<a name="cl-3586"></a>            serie.axis = 'left';
<a name="cl-3587"></a>            serie.highlight = true;
<a name="cl-3588"></a>            serie.tips = {
<a name="cl-3589"></a>              width     : 100,
<a name="cl-3590"></a>              trackMouse: true,
<a name="cl-3591"></a>              renderer: function(storeItem, item) {
<a name="cl-3592"></a>                  this.setTitle('&lt;b&gt;'+item.series.yField + '&lt;/b&gt;: ' + storeItem.get(item.series.yField) + '&lt;br&gt;' + Ext.Date.format(storeItem.get('time'), 'd M Y'));
<a name="cl-3593"></a>              }
<a name="cl-3594"></a>            };
<a name="cl-3595"></a>            return serie;
<a name="cl-3596"></a>        });
<a name="cl-3597"></a>        
<a name="cl-3598"></a>        return this.series;
<a name="cl-3599"></a>    },
<a name="cl-3600"></a>    
<a name="cl-3601"></a>    setTimeInterval : function() {
<a name="cl-3602"></a>        var dateFrom = this.query('#dateFrom').shift(),
<a name="cl-3603"></a>            dateTo = this.query('#dateTo').shift(),
<a name="cl-3604"></a>            interval = this.intervalType,
<a name="cl-3605"></a>            fromDate = dateFrom.getValue(),
<a name="cl-3606"></a>            toDate;
<a name="cl-3607"></a>
<a name="cl-3608"></a>        if (interval == 'day') {
<a name="cl-3609"></a>            toDate = Ext.Date.add(fromDate, Ext.Date.DAY, 1);
<a name="cl-3610"></a>            dateTo.setValue(toDate);
<a name="cl-3611"></a>            dateTo.disable();
<a name="cl-3612"></a>        } else if (interval == 'week') {
<a name="cl-3613"></a>            toDate = Ext.Date.add(fromDate, Ext.Date.DAY, 7);
<a name="cl-3614"></a>            dateTo.setValue(toDate);
<a name="cl-3615"></a>            dateTo.disable();
<a name="cl-3616"></a>        } else if (interval == 'month') {
<a name="cl-3617"></a>            toDate = Ext.Date.add(fromDate, Ext.Date.MONTH, 1);
<a name="cl-3618"></a>            dateTo.setValue(toDate);
<a name="cl-3619"></a>            dateTo.disable();
<a name="cl-3620"></a>        } else if (interval == 'custom') {
<a name="cl-3621"></a>            dateTo.enable();
<a name="cl-3622"></a>        }
<a name="cl-3623"></a>        
<a name="cl-3624"></a>        dateTo.setMinValue(fromDate);
<a name="cl-3625"></a>    },
<a name="cl-3626"></a>    
<a name="cl-3627"></a>    getFieldList : function() {
<a name="cl-3628"></a>        var notAdd  = ['time'],
<a name="cl-3629"></a>            series = this.series,
<a name="cl-3630"></a>            fields = [];
<a name="cl-3631"></a>        
<a name="cl-3632"></a>        Ext.Array.each(series, function(item) {
<a name="cl-3633"></a>            fields.push(item.yField);
<a name="cl-3634"></a>        });
<a name="cl-3635"></a>        return fields;
<a name="cl-3636"></a>    },
<a name="cl-3637"></a>    
<a name="cl-3638"></a>    buildStore : function() {
<a name="cl-3639"></a>        var model = 'C8Y.model.GraphModel' + Ext.id();
<a name="cl-3640"></a>        Ext.define(model,{
<a name="cl-3641"></a>            extend  : 'Ext.data.Model',
<a name="cl-3642"></a>            fields  : Ext.Array.merge([ {name: 'time', type:'date'}], this.getFieldList())
<a name="cl-3643"></a>        });
<a name="cl-3644"></a>        
<a name="cl-3645"></a>        return Ext.create('Ext.data.Store', {
<a name="cl-3646"></a>            model   : model,
<a name="cl-3647"></a>            sorters : [
<a name="cl-3648"></a>                { property: 'time', direction: 'ASC'}
<a name="cl-3649"></a>            ]
<a name="cl-3650"></a>        });
<a name="cl-3651"></a>    },
<a name="cl-3652"></a>    
<a name="cl-3653"></a>    loadData : function(cfg) {
<a name="cl-3654"></a>        var me = this,
<a name="cl-3655"></a>            store = this.store,
<a name="cl-3656"></a>            fields = this.getFieldList();
<a name="cl-3657"></a>        
<a name="cl-3658"></a>        cfg = Ext.applyIf(cfg, (this.defaultLoadCfg || {}));
<a name="cl-3659"></a>        this.lastLoadCfg = cfg;
<a name="cl-3660"></a>        store.removeAll();
<a name="cl-3661"></a>        me.el.mask('loading');
<a name="cl-3662"></a>        C8Y.client.measurement.list(cfg, function(r) {
<a name="cl-3663"></a>            var m = r.measurements,
<a name="cl-3664"></a>                items;
<a name="cl-3665"></a>            
<a name="cl-3666"></a>            items = Ext.Array.map(m, function(item) {
<a name="cl-3667"></a>                var data = {
<a name="cl-3668"></a>                    time    : item.time
<a name="cl-3669"></a>                };
<a name="cl-3670"></a>                
<a name="cl-3671"></a>                Ext.Array.each(fields, function(field) {
<a name="cl-3672"></a>                    data[field] = item[me.measurementProperty][field].value;
<a name="cl-3673"></a>                });
<a name="cl-3674"></a>                
<a name="cl-3675"></a>                return Ext.create(store.model,data);
<a name="cl-3676"></a>            });
<a name="cl-3677"></a>            
<a name="cl-3678"></a>            store.add(items);
<a name="cl-3679"></a>            me.el.unmask();
<a name="cl-3680"></a>        });
<a name="cl-3681"></a>    },
<a name="cl-3682"></a>    
<a name="cl-3683"></a>    onDateChange : function() { 
<a name="cl-3684"></a>        var dateFrom = this.query('#dateFrom').shift().getValue(),
<a name="cl-3685"></a>            dateTo = this.query('#dateTo').shift().getValue();
<a name="cl-3686"></a>        
<a name="cl-3687"></a>        this.defaultLoadCfg.dateFrom = dateFrom;
<a name="cl-3688"></a>        this.defaultLoadCfg.dateTo = dateTo;
<a name="cl-3689"></a>        
<a name="cl-3690"></a>        if (this.lastLoadCfg) {
<a name="cl-3691"></a>            this.loadData({
<a name="cl-3692"></a>                source : this.lastLoadCfg.source
<a name="cl-3693"></a>            });
<a name="cl-3694"></a>        }
<a name="cl-3695"></a>    }
<a name="cl-3696"></a>});
<a name="cl-3697"></a>/**
<a name="cl-3698"></a>* @class C8Y.app.CardPanel
<a name="cl-3699"></a>* Manages the main application views
<a name="cl-3700"></a>* @extends Ext.panel.Panel
<a name="cl-3701"></a>* @xtype c8ycardpanel
<a name="cl-3702"></a>*/
<a name="cl-3703"></a>Ext.define('C8Y.app.CardPanel',{
<a name="cl-3704"></a>   extend   : 'Ext.panel.Panel',
<a name="cl-3705"></a>   alias    : 'widget.c8ycardpanel',
<a name="cl-3706"></a>   requires : [
<a name="cl-3707"></a>        'Ext.layout.container.Card',
<a name="cl-3708"></a>        'Ext.toolbar.Toolbar',
<a name="cl-3709"></a>        'Ext.button.Button'
<a name="cl-3710"></a>   ],
<a name="cl-3711"></a>   config   : {
<a name="cl-3712"></a>       menu : ['Menu not defined']
<a name="cl-3713"></a>   },
<a name="cl-3714"></a>   
<a name="cl-3715"></a>   initComponent : function() {
<a name="cl-3716"></a>       this.deferredRender = true;
<a name="cl-3717"></a>       this.layout = 'card';
<a name="cl-3718"></a>       this.bodyCls = 'C8Ymain';
<a name="cl-3719"></a>       this.border = false;
<a name="cl-3720"></a>       this.callParent(arguments);
<a name="cl-3721"></a>   },
<a name="cl-3722"></a>   
<a name="cl-3723"></a>   buildToolbar : function(menu) {
<a name="cl-3724"></a>       return [{
<a name="cl-3725"></a>              xtype    : 'toolbar',
<a name="cl-3726"></a>              height   : 46,
<a name="cl-3727"></a>              cls      : 'C8Ymaintoolbar',
<a name="cl-3728"></a>              // bodyStyle: 'background: #650065 none !important;',
<a name="cl-3729"></a>              // style    : 'background: #650065 none !important;',
<a name="cl-3730"></a>              dock     : 'top',
<a name="cl-3731"></a>              itemId   : 'tbar',
<a name="cl-3732"></a>              defaults : {
<a name="cl-3733"></a>                 xtype    : 'button',
<a name="cl-3734"></a>                 scope    : this,
<a name="cl-3735"></a>                 cls      : 'C8Ymainmenubtn',
<a name="cl-3736"></a>                 margin   : '0 0 0 9px',
<a name="cl-3737"></a>                 handler  : this.onMenuClick
<a name="cl-3738"></a>              },
<a name="cl-3739"></a>              items    : menu
<a name="cl-3740"></a>        }];
<a name="cl-3741"></a>   },
<a name="cl-3742"></a>   
<a name="cl-3743"></a>   makeToolbar : function(menu) {
<a name="cl-3744"></a>       this.addDocked(this.buildToolbar(menu));
<a name="cl-3745"></a>   },
<a name="cl-3746"></a>   
<a name="cl-3747"></a>   deleteToolbar : function() {
<a name="cl-3748"></a>       var tbar = this.getDockedComponent('tbar');
<a name="cl-3749"></a>       this.removeDocked(tbar, true);
<a name="cl-3750"></a>   },
<a name="cl-3751"></a>   
<a name="cl-3752"></a>   onMenuClick : function(btn, e, opt) {
<a name="cl-3753"></a>       this.getLayout().setActiveItem(btn.option);
<a name="cl-3754"></a>   }
<a name="cl-3755"></a>});
<a name="cl-3756"></a>/**
<a name="cl-3757"></a>* @class C8Y.app.Viewport
<a name="cl-3758"></a>* Main Application viewport containing the default header and footer
<a name="cl-3759"></a>* @extends Ext.container.Viewport
<a name="cl-3760"></a>*/
<a name="cl-3761"></a>Ext.define('C8Y.app.Viewport', {
<a name="cl-3762"></a>    
<a name="cl-3763"></a>   extend   : 'Ext.container.Viewport',
<a name="cl-3764"></a>
<a name="cl-3765"></a>   requires : [
<a name="cl-3766"></a>        'Ext.layout.container.Border',
<a name="cl-3767"></a>        'C8Y.app.Header',
<a name="cl-3768"></a>        'C8Y.app.Footer',
<a name="cl-3769"></a>        'C8Y.app.CardPanel'
<a name="cl-3770"></a>   ],
<a name="cl-3771"></a>   
<a name="cl-3772"></a>   //Overrides eventual configuration passed at instantiation time
<a name="cl-3773"></a>   initComponent : function() {
<a name="cl-3774"></a>       this.layout = 'border';
<a name="cl-3775"></a>	   this.bodyStyle = 'background:transparent';
<a name="cl-3776"></a>	   this.style = 'background:transparent';
<a name="cl-3777"></a>	   this.border = false;
<a name="cl-3778"></a>       this.items = this.buildItems();
<a name="cl-3779"></a>       this.callParent(arguments);
<a name="cl-3780"></a>   },
<a name="cl-3781"></a>   
<a name="cl-3782"></a>   buildItems : function() {
<a name="cl-3783"></a>       return [
<a name="cl-3784"></a>           {
<a name="cl-3785"></a>               xtype   : 'c8yheader',
<a name="cl-3786"></a>               region  : 'north',
<a name="cl-3787"></a>               app     : this.app
<a name="cl-3788"></a>           },
<a name="cl-3789"></a>           {
<a name="cl-3790"></a>               xtype   : 'c8ycardpanel',
<a name="cl-3791"></a>               region  : 'center'
<a name="cl-3792"></a>           },
<a name="cl-3793"></a>           {
<a name="cl-3794"></a>               xtype   : 'c8yfooter',
<a name="cl-3795"></a>               region  : 'south',
<a name="cl-3796"></a>               app     : this.app
<a name="cl-3797"></a>           }
<a name="cl-3798"></a>       ];
<a name="cl-3799"></a>   },
<a name="cl-3800"></a>   
<a name="cl-3801"></a>   /**
<a name="cl-3802"></a>   * @returns null
<a name="cl-3803"></a>   * Starts de application views. This delayed because it assumes that you must be authenticated to use the application
<a name="cl-3804"></a>   */
<a name="cl-3805"></a>   start : function() {
<a name="cl-3806"></a>       var card = this.query('c8ycardpanel').pop(),
<a name="cl-3807"></a>           tbar = card.buildToolbar(),
<a name="cl-3808"></a>           me = this;
<a name="cl-3809"></a>        Ext.each(this.mainitems, function(val, ix) {
<a name="cl-3810"></a>           val.itemId = val.itemId || 'c8ymainview'+ix;
<a name="cl-3811"></a>           val.app = this.app;
<a name="cl-3812"></a>        });
<a name="cl-3813"></a>       
<a name="cl-3814"></a>       card.makeToolbar(me.menu);
<a name="cl-3815"></a>       card.add(this.mainitems);
<a name="cl-3816"></a>       card.doLayout();
<a name="cl-3817"></a>   },
<a name="cl-3818"></a>   
<a name="cl-3819"></a>   end : function() {
<a name="cl-3820"></a>       var card = this.query('c8ycardpanel').pop();
<a name="cl-3821"></a>       card.deleteToolbar();
<a name="cl-3822"></a>       card.removeAll();
<a name="cl-3823"></a>   }
<a name="cl-3824"></a>});
<a name="cl-3825"></a>/**
<a name="cl-3826"></a> * @class C8Y.app.Application
<a name="cl-3827"></a> * Base class that provides application viewport
<a name="cl-3828"></a> * @extends Ext.app.Application
<a name="cl-3829"></a> */
<a name="cl-3830"></a>Ext.define('C8Y.app.Application', {
<a name="cl-3831"></a>    
<a name="cl-3832"></a>   extend   : 'Ext.app.Application',
<a name="cl-3833"></a>   
<a name="cl-3834"></a>   requires : [
<a name="cl-3835"></a>        'C8Y.app.Viewport',
<a name="cl-3836"></a>        'C8Y.app.LoginWindow',
<a name="cl-3837"></a>        'Ext.data.proxy.Rest',
<a name="cl-3838"></a>        'Ext.data.reader.Reader'
<a name="cl-3839"></a>   ],
<a name="cl-3840"></a>   
<a name="cl-3841"></a>   /**
<a name="cl-3842"></a>   * @cfg {Array} items (required)
<a name="cl-3843"></a>   * List of items that should rendered into the main view
<a name="cl-3844"></a>   */
<a name="cl-3845"></a>   /**
<a name="cl-3846"></a>   * @cfg {Array} menu
<a name="cl-3847"></a>   * List or single items to populate the menu to switch between the views. If not provided one will be generated from the {@link #items} configuration.
<a name="cl-3848"></a>   */
<a name="cl-3849"></a>   onBeforeLaunch : function() {
<a name="cl-3850"></a>       //Create the viewport including header and footer
<a name="cl-3851"></a>       var viewport = Ext.create('C8Y.app.Viewport', {
<a name="cl-3852"></a>                app         : this,
<a name="cl-3853"></a>                mainitems   : this.items,
<a name="cl-3854"></a>                menu        : this.buildMenu()
<a name="cl-3855"></a>           }),
<a name="cl-3856"></a>       //Create the login view
<a name="cl-3857"></a>           loginwindow = Ext.create('C8Y.app.LoginWindow', {
<a name="cl-3858"></a>                listeners : {
<a name="cl-3859"></a>                    scope   : this,
<a name="cl-3860"></a>                    trylogin   : this.onTryLogin
<a name="cl-3861"></a>                }
<a name="cl-3862"></a>           });
<a name="cl-3863"></a>       
<a name="cl-3864"></a>	   Ext.get('c8ypageloading').remove();
<a name="cl-3865"></a>		
<a name="cl-3866"></a>       this.viewport = viewport;
<a name="cl-3867"></a>       this.loginwindow = loginwindow;
<a name="cl-3868"></a>       
<a name="cl-3869"></a>       this.autoCreateViewport = false;
<a name="cl-3870"></a>       this.callParent(arguments);
<a name="cl-3871"></a>       this.on('logout', this.onLogout, this);
<a name="cl-3872"></a>       C8Y.client.evtbus.addListener('loginfailed', Ext.Function.bind(this.onLoginFail, this));
<a name="cl-3873"></a>       C8Y.client.evtbus.addListener('login', Ext.Function.bind(this.onLogin, this));
<a name="cl-3874"></a>   },
<a name="cl-3875"></a>   
<a name="cl-3876"></a>   /**
<a name="cl-3877"></a>   * @return {Object} Returns the passed menu configuration or builds one from .
<a name="cl-3878"></a>   * 
<a name="cl-3879"></a>   */
<a name="cl-3880"></a>   buildMenu : function() {
<a name="cl-3881"></a>       var menu = [],
<a name="cl-3882"></a>           items,item,i;
<a name="cl-3883"></a>       if (!this.menu) {
<a name="cl-3884"></a>           if (!this.items) throw('You must define items for the C8Y.app.Application.');
<a name="cl-3885"></a>           items = this.items;
<a name="cl-3886"></a>           for (i=0,t=items.length;i&lt;t;i++) {
<a name="cl-3887"></a>               item = items[i];
<a name="cl-3888"></a>               menu.push({
<a name="cl-3889"></a>                    option  : item.itemId || 'c8ymainview'+i,
<a name="cl-3890"></a>                    text    : item.menuOption || item.title || item.xtype,
<a name="cl-3891"></a>                    scale   : 'medium',
<a name="cl-3892"></a>                    iconCls : item.iconCls,
<a name="cl-3893"></a>                    padding : '4px 5px',
<a name="cl-3894"></a>                    toggleGroup  : 'c8ymainmenu',
<a name="cl-3895"></a>                    enableToggle : true,
<a name="cl-3896"></a>                    pressed      : (i==0)
<a name="cl-3897"></a>               });
<a name="cl-3898"></a>           }
<a name="cl-3899"></a>       }
<a name="cl-3900"></a>       return this.menu || menu;
<a name="cl-3901"></a>   },
<a name="cl-3902"></a>   
<a name="cl-3903"></a>   //Fires on login
<a name="cl-3904"></a>   onTryLogin : function(login, password, tenant) {
<a name="cl-3905"></a>	   // Tenant is now read from the login form
<a name="cl-3906"></a>       // var tenant = Ext.get('tenant').dom.innerHTML,
<a name="cl-3907"></a>		var me = this;
<a name="cl-3908"></a>       C8Y.client.auth.login(tenant, login, password);
<a name="cl-3909"></a>   },
<a name="cl-3910"></a>   
<a name="cl-3911"></a>   onLogin : function(userData) {
<a name="cl-3912"></a>       this.userData = userData;
<a name="cl-3913"></a>       this.loginwindow.closeWindow();
<a name="cl-3914"></a>       this.viewport.start();
<a name="cl-3915"></a>       delete this.loginwindow;
<a name="cl-3916"></a>       this.fireEvent('login', userData);
<a name="cl-3917"></a>   },
<a name="cl-3918"></a>   
<a name="cl-3919"></a>   onLogout : function() {
<a name="cl-3920"></a>       var loginwindow = Ext.create('C8Y.app.LoginWindow', {
<a name="cl-3921"></a>               listeners : {
<a name="cl-3922"></a>                   scope   : this,
<a name="cl-3923"></a>                   trylogin   : this.onTryLogin
<a name="cl-3924"></a>               }
<a name="cl-3925"></a>       });
<a name="cl-3926"></a>       this.viewport.end();
<a name="cl-3927"></a>       this.loginwindow = loginwindow;
<a name="cl-3928"></a>   },
<a name="cl-3929"></a>   
<a name="cl-3930"></a>   onLoginFail : function() {
<a name="cl-3931"></a>        Ext.Msg.show({
<a name="cl-3932"></a>            title  : 'Login Error',
<a name="cl-3933"></a>            msg    : 'Wrong username or password',
<a name="cl-3934"></a>            buttons: Ext.Msg.OK,
<a name="cl-3935"></a>            icon   : Ext.Msg.ERROR
<a name="cl-3936"></a>        });
<a name="cl-3937"></a>        this.loginwindow.resetBtn();
<a name="cl-3938"></a>   }
<a name="cl-3939"></a>});
<a name="cl-3940"></a>var C8Y = C8Y || {};
<a name="cl-3941"></a>/**
<a name="cl-3942"></a> * @class C8Y.application
<a name="cl-3943"></a> *
<a name="cl-3944"></a> * This is the application Facade
<a name="cl-3945"></a> * @singleton
<a name="cl-3946"></a> */
<a name="cl-3947"></a>
<a name="cl-3948"></a>/**
<a name="cl-3949"></a>* @method application
<a name="cl-3950"></a>* Creates the application
<a name="cl-3951"></a>* @param config {Object} configuration for the application
<a name="cl-3952"></a>*/
<a name="cl-3953"></a>
<a name="cl-3954"></a>C8Y.application = function(config) {
<a name="cl-3955"></a>    Ext.require('C8Y.app.Application');
<a name="cl-3956"></a>	Ext.onReady(function() {
<a name="cl-3957"></a>		Ext.create('C8Y.app.Application', config);
<a name="cl-3958"></a>		Ext.apply(Ext.form.field.VTypes, {
<a name="cl-3959"></a>                daterange: function(val, field) {
<a name="cl-3960"></a>                    var date = field.parseDate(val);
<a name="cl-3961"></a>
<a name="cl-3962"></a>                    if (!date) {
<a name="cl-3963"></a>                        return false;
<a name="cl-3964"></a>                    }
<a name="cl-3965"></a>                    if (field.startDateField &amp;&amp; (!this.dateRangeMax || (date.getTime() != this.dateRangeMax.getTime()))) {
<a name="cl-3966"></a>                        var start = field.up('form').down('#' + field.startDateField);
<a name="cl-3967"></a>                        start.setMaxValue(date);
<a name="cl-3968"></a>                        start.validate();
<a name="cl-3969"></a>                        this.dateRangeMax = date;
<a name="cl-3970"></a>                    }
<a name="cl-3971"></a>                    else if (field.endDateField &amp;&amp; (!this.dateRangeMin || (date.getTime() != this.dateRangeMin.getTime()))) {
<a name="cl-3972"></a>                        var end = field.up('form').down('#' + field.endDateField);
<a name="cl-3973"></a>                        end.setMinValue(date);
<a name="cl-3974"></a>                        end.validate();
<a name="cl-3975"></a>                        this.dateRangeMin = date;
<a name="cl-3976"></a>                    }
<a name="cl-3977"></a>                    /*
<a name="cl-3978"></a>                     * Always return true since we're only using this vtype to set the
<a name="cl-3979"></a>                     * min/max allowed values (these are tested for after the vtype test)
<a name="cl-3980"></a>                     */
<a name="cl-3981"></a>                    return true;
<a name="cl-3982"></a>                },
<a name="cl-3983"></a>
<a name="cl-3984"></a>                daterangeText: 'Start date must be less than end date',
<a name="cl-3985"></a>
<a name="cl-3986"></a>                password: function(val, field) {
<a name="cl-3987"></a>                    if (field.initialPassField) {
<a name="cl-3988"></a>                        var form = field.up('form'),
<a name="cl-3989"></a>                            pwd = form.query("#"+field.initialPassField).shift();
<a name="cl-3990"></a>                        return (val == pwd.getValue());
<a name="cl-3991"></a>                    }
<a name="cl-3992"></a>                    return true;
<a name="cl-3993"></a>                },
<a name="cl-3994"></a>
<a name="cl-3995"></a>                passwordText: 'Passwords do not match'
<a name="cl-3996"></a>            });
<a name="cl-3997"></a>	});
<a name="cl-3998"></a>};
<a name="cl-3999"></a>
<a name="cl-4000"></a>
<a name="cl-4001"></a>
</pre></div></td></tr></table>

    </div>
  
  </div>
  


  <div id="mask"><div></div></div>

  </div>

      </div>
    </div>

  </div>

  <div id="footer">
    <ul id="footer-nav">
      <li>Copyright © 2011 <a href="http://atlassian.com">Atlassian</a></li>
      <li><a href="http://www.atlassian.com/hosted/terms.jsp">Terms of Service</a></li>
      <li><a href="http://www.atlassian.com/about/privacy.jsp">Privacy</a></li>
      <li><a href="//bitbucket.org/site/master/issues/new">Report a Bug to Bitbucket</a></li>
      <li><a href="http://confluence.atlassian.com/x/IYBGDQ">API</a></li>
      <li><a href="http://status.bitbucket.org/">Server Status</a></li>
    </ul>
    <ul id="social-nav">
      <li class="blog"><a href="http://blog.bitbucket.org">Bitbucket Blog</a></li>
      <li class="twitter"><a href="http://www.twitter.com/bitbucket">Twitter</a></li>
    </ul>
    <h5>We run</h5>
    <ul id="technologies">
      <li><a href="http://www.djangoproject.com/">Django 1.3.1</a></li>
      <li><a href="//bitbucket.org/jespern/django-piston/">Piston 0.3dev</a></li>
      <li><a href="http://git-scm.com/">Git 1.7.6</a></li>
      <li><a href="http://www.selenic.com/mercurial/">Hg 1.9.2</a></li>
      <li><a href="http://www.python.org">Python 2.7.2</a></li>
      <li>01bd985291d3 | bitbucket05</li>
    </ul>
  </div>

  <script src="https://dwz7u9t8u8usb.cloudfront.net/m/8fca063a0549/js/lib/global.js"></script>






  <script>
    BB.gaqPush(['_trackPageview']);
  
    BB.gaqPush(['atl._trackPageview']);

    

    

    (function () {
        var ga = document.createElement('script');
        ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
        ga.setAttribute('async', 'true');
        document.documentElement.firstChild.appendChild(ga);
    }());
  </script>

</body>
</html>
